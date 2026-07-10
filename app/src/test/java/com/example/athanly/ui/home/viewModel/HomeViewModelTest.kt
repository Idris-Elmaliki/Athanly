package com.example.athanly.ui.home.viewModel

import app.cash.turbine.test
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import com.example.athanly.athanTime.local.domain.modelData.mockCities
import com.example.athanly.athanTime.local.domain.modelData.mockSchedulesByCityId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class HomeViewModelTest {

    // Its own scheduler, deliberately NOT shared with runTest(...) below. HomeViewModel's
    // init launches an infinite `while (true) { tick(); delay(1000) }` loop — if it shared a
    // scheduler with runTest, runTest's implicit advanceUntilIdle() at completion would spin
    // forever trying to drain a queue that never empties. Unconfined (not Standard) so the
    // loop still runs eagerly up to its first delay(1000) — i.e. one tick() happens
    // synchronously when the ViewModel is constructed, so tests see a freshly computed
    // nextPrayerInfo immediately without manually advancing virtual time.
    private val mainDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // viewModelScope uses Dispatchers.Main internally; it isn't available by default in
        // JVM unit tests, so this substitutes the test dispatcher before any ViewModel
        // (which launches a coroutine in init) is constructed
        Dispatchers.setMain(mainDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Cities/schedule-only tests don't care what "now" is, so they get the real clock;
    // countdown-specific tests below pass their own fixed clock via fixedClockAt(...)
    private fun homeViewModel(clock: Clock = Clock.systemDefaultZone()) = HomeViewModel(clock = clock)

    @Test
    fun `homeState defaults to mock cities and schedules with the first city selected`() = runTest {
        // Turbine's .test {} subscribes to the StateFlow and lets us step through
        // its emissions one at a time via awaitItem(), instead of manually collecting
        homeViewModel().homeState.test {
            val state = awaitItem() // the StateFlow's current value, emitted immediately

            assertEquals(mockCities, state.cities)
            assertEquals(mockSchedulesByCityId, state.schedulesByCityId)
            assertEquals(0, state.selectedCityIndex)
            assertEquals(false, state.isLoading)
            assertEquals(null, state.error)
        }
    }

    @Test
    fun `onCitySelected(2) updates selectedCityIndex to 2`() = runTest {
        val viewModel = homeViewModel()
        viewModel.homeState.test {
            awaitItem() // consume the initial default state first

            viewModel.onCitySelected(index = 2)

            // .update {} on the ViewModel's MutableStateFlow emits a new value,
            // which Turbine surfaces here as the next item to await
            val updated = awaitItem()
            assertEquals(2, updated.selectedCityIndex)
        }
    }

    @Test
    fun `nextPrayerInfo is Asr when now is between Istanbul's Dhuhr and Asr`() = runTest {
        // Istanbul's mock schedule: dhuhr = 12:15, asr = 15:45. 14:00 sits exactly halfway
        // through that window, which gives a clean progressFraction to assert on (0.5f)
        val now = LocalTime.of(14, 0)

        homeViewModel(clock = fixedClockAt(now)).homeState.test {
            val info = awaitItem().nextPrayerInfo

            assertEquals(Prayer.DHUHR, info?.currentPrayer)
            assertEquals(Prayer.ASR, info?.nextPrayer)
            assertEquals(LocalTime.of(15, 45), info?.startsAt)
            assertEquals(Duration.ofHours(1).plusMinutes(45), info?.remaining)
            assertEquals(0.5f, info?.progressFraction)
        }
    }

    // Builds a Clock that always reports the given time-of-day, so countdown math is
    // deterministic instead of depending on when the test happens to run
    private fun fixedClockAt(time: LocalTime): Clock {
        val zone = ZoneId.of("UTC")
        val instant = LocalDate.of(2026, 1, 1).atTime(time).atZone(zone).toInstant()
        return Clock.fixed(instant, zone)
    }
}