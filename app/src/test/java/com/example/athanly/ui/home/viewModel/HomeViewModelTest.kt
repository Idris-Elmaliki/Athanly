package com.example.athanly.ui.home.viewModel

import app.cash.turbine.test
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.athanTime.domain.PrayerSchedule
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import com.example.athanly.athanTime.local.domain.repository.AthanTimeRepository
import com.example.athanly.athanTime.remote.domain.repository.AthanApiRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
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

    // City/schedule-only tests don't care what "now" is, so they get the real clock;
    // countdown-specific tests below pass their own fixed clock via fixedClockAt(...)
    //
    // Every dependency gets a mockk() default so tests that don't care about it (e.g.
    // the countdown test below) don't have to stub all three just to construct a
    // ViewModel. `athanTimeRepository`'s defaults describe a location-less DB — the
    // state before onboarding's Location step has run — and `preferencesDataStore`'s
    // default is Shafi (non-Hanafi), matching AppDataUiState's own default.
    private fun homeViewModel(
        clock: Clock = Clock.systemDefaultZone(),
        athanTimeRepository: AthanTimeRepository = mockk {
            every { observeCity() } returns flowOf(null)
            every { observeSchedule() } returns flowOf(null)
        },
        athanApiRepository: AthanApiRepository = mockk(),
        preferencesDataStore: AppDataPreferencesDataStore = mockk {
            every { observedIsHanafiMode() } returns flowOf(false)
        },
    ) = HomeViewModel(
        clock = clock,
        athanTimeRepository = athanTimeRepository,
        athanApiRepository = athanApiRepository,
        preferencesDataStore = preferencesDataStore,
    )

    @Test
    fun `homeState renders the city and schedule straight from the local repository's Flows`() = runTest {
        val istanbul = City(name = "Istanbul", hijriDate = "12 Rabi' al-Awwal, 1445 AH")
        val schedule = PrayerSchedule(
            fajr = LocalTime.of(4, 30),
            sunrise = LocalTime.of(6, 5),
            dhuhr = LocalTime.of(12, 15),
            asr = LocalTime.of(15, 45),
            maghrib = LocalTime.of(18, 20),
            isha = LocalTime.of(19, 45),
        )

        // mockk<AthanTimeRepository> { every { ... } returns ... } builds a fake
        // repository whose Flow-returning methods emit exactly what we hand them,
        // the same pattern SettingsViewModelTest already uses for its DataStore fake.
        // schedule.date defaults to today (PrayerSchedule's own default), so the
        // ViewModel's staleness check will see this as fresh and never call the API.
        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeCity() } returns flowOf(istanbul)
            every { observeSchedule() } returns flowOf(schedule)
        }

        homeViewModel(athanTimeRepository = athanTimeRepository).homeState.test {
            val state = awaitItem() // the StateFlow's current value, emitted immediately

            assertEquals(istanbul, state.city)
            assertEquals(schedule, state.schedule)
            assertEquals(false, state.isLoading)
            assertEquals(null, state.error)
        }
    }

    @Test
    fun `homeState stays empty when no city has been saved yet`() = runTest {
        // Default homeViewModel() dependencies already describe a location-less DB — this
        // asserts the ViewModel doesn't crash or invent placeholder data while waiting for
        // onboarding's Location step to run
        homeViewModel().homeState.test {
            val state = awaitItem()

            assertEquals(null, state.city)
            assertEquals(null, state.schedule)
            assertEquals(null, state.nextPrayerInfo)
        }
    }

    @Test
    fun `nextPrayerInfo is Asr when now is between Istanbul's Dhuhr and Asr`() = runTest {
        // Istanbul's schedule: dhuhr = 12:15, asr = 15:45. 14:00 sits exactly halfway
        // through that window, which gives a clean progressFraction to assert on (0.5f)
        val now = LocalTime.of(14, 0)
        val clock = fixedClockAt(now)
        val istanbul = City(name = "Istanbul", hijriDate = "12 Rabi' al-Awwal, 1445 AH")

        // date is pinned to the fixed clock's own date (2026-01-01) rather than the
        // PrayerSchedule default of LocalDate.now() — otherwise the ViewModel's staleness
        // check would see this as "not today" and try to refetch from the (unstubbed) API
        val schedule = PrayerSchedule(
            date = LocalDate.of(2026, 1, 1),
            fajr = LocalTime.of(4, 30),
            sunrise = LocalTime.of(6, 5),
            dhuhr = LocalTime.of(12, 15),
            asr = LocalTime.of(15, 45),
            maghrib = LocalTime.of(18, 20),
            isha = LocalTime.of(19, 45),
        )
        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeCity() } returns flowOf(istanbul)
            every { observeSchedule() } returns flowOf(schedule)
        }

        homeViewModel(clock = clock, athanTimeRepository = athanTimeRepository).homeState.test {
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

    // Reused by the sync/staleness tests below — same city every time, only its
    // cached schedule (or lack of one) changes per test
    private val istanbul = City(
        name = "Istanbul",
        hijriDate = "12 Rabi' al-Awwal, 1445 AH",
        latitude = 41.0,
        longitude = 28.9,
    )

    private fun scheduleOn(date: LocalDate) = PrayerSchedule(
        date = date,
        fajr = LocalTime.of(4, 30),
        sunrise = LocalTime.of(6, 5),
        dhuhr = LocalTime.of(12, 15),
        asr = LocalTime.of(15, 45),
        maghrib = LocalTime.of(18, 20),
        isha = LocalTime.of(19, 45),
    )

    @Test
    fun `a city with no cached schedule triggers a fetch from the API and a save to the local repository`() = runTest {
        val today = LocalDate.now()
        val fetchedSchedule = scheduleOn(today)

        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeCity() } returns flowOf(istanbul)
            every { observeSchedule() } returns flowOf(null) // nothing cached yet
            // saveCityWithSchedule is `suspend fun ... : Unit` — coEvery { ... } just Runs
            // stubs it to do nothing and return Unit, same as SettingsViewModelTest's
            // coEvery {...} just Runs for its DataStore setters
            coEvery { saveCityWithSchedule(any(), any()) } just Runs
        }
        val athanApiRepository = mockk<AthanApiRepository> {
            coEvery {
                fetchPrayerSchedule(
                    latitude = 41.0,
                    longitude = 28.9,
                    date = today,
                    useHanafiAsr = false
                )
            } returns fetchedSchedule
        }

        homeViewModel(
            athanTimeRepository = athanTimeRepository,
            athanApiRepository = athanApiRepository,
        )

        // coVerify confirms these two suspend calls actually happened with these exact
        // arguments. There's nothing to assert on homeState here — the mocked
        // observeSchedule() Flow doesn't change after the save the way a real Room
        // Flow would, so the fetch+save calls themselves are what this test is about.
        coVerify {
            athanApiRepository.fetchPrayerSchedule(
                latitude = 41.0,
                longitude = 28.9,
                date = today,
                useHanafiAsr = false
            )
            athanTimeRepository.saveCityWithSchedule(istanbul, fetchedSchedule)
        }
    }

    @Test
    fun `a schedule cached for a previous day is treated as stale and refetched`() = runTest {
        val today = LocalDate.now()
        val staleSchedule = scheduleOn(today.minusDays(1))
        val freshSchedule = scheduleOn(today)

        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeCity() } returns flowOf(istanbul)
            every { observeSchedule() } returns flowOf(staleSchedule)
            coEvery { saveCityWithSchedule(any(), any()) } just Runs
        }
        val athanApiRepository = mockk<AthanApiRepository> {
            coEvery { fetchPrayerSchedule(any(), any(), any(), any()) } returns freshSchedule
        }

        homeViewModel(
            athanTimeRepository = athanTimeRepository,
            athanApiRepository = athanApiRepository,
        )

        coVerify { athanTimeRepository.saveCityWithSchedule(istanbul, freshSchedule) }
    }

    @Test
    fun `a schedule already cached for today is left alone — no fetch happens`() = runTest {
        val today = LocalDate.now()
        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeCity() } returns flowOf(istanbul)
            every { observeSchedule() } returns flowOf(scheduleOn(today))
        }
        // Left unstubbed on purpose: coVerify below asserts it's never called at all,
        // so there's nothing that needs a return value stubbed.
        val athanApiRepository = mockk<AthanApiRepository>()

        homeViewModel(
            athanTimeRepository = athanTimeRepository,
            athanApiRepository = athanApiRepository,
        )

        coVerify(exactly = 0) { athanApiRepository.fetchPrayerSchedule(any(), any(), any(), any()) }
    }

    @Test
    fun `no city saved yet means no fetch is attempted`() = runTest {
        // Default homeViewModel() dependencies already describe observeCity() emitting null —
        // this just makes the "why" explicit as its own named test rather than only being
        // implied by the homeState-stays-empty test above
        val athanApiRepository = mockk<AthanApiRepository>()

        homeViewModel(athanApiRepository = athanApiRepository)

        coVerify(exactly = 0) { athanApiRepository.fetchPrayerSchedule(any(), any(), any(), any()) }
    }

    @Test
    fun `a failed fetch surfaces its message on homeState's error instead of crashing`() = runTest {
        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeCity() } returns flowOf(istanbul)
            every { observeSchedule() } returns flowOf(null)
        }
        val athanApiRepository = mockk<AthanApiRepository> {
            coEvery { fetchPrayerSchedule(any(), any(), any(), any()) } throws RuntimeException("network down")
        }

        homeViewModel(
            athanTimeRepository = athanTimeRepository,
            athanApiRepository = athanApiRepository,
        ).homeState.test {
            assertEquals("network down", awaitItem().error)
        }
    }
}