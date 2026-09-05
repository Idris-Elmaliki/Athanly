package com.example.athanly.athanTime.domain

import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.local.domain.repository.AthanTimeRepository
import com.example.athanly.athanTime.remote.domain.repository.AthanApiRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class RefreshPrayerScheduleUseCaseTest {

    // Reused across every test below — only the cached schedule's date/isHanafiMode change
    // per test, so this fixture keeps each test focused on just what it's actually asserting
    private val istanbul = City(
        name = "Istanbul",
        hijriDate = "12 Rabi' al-Awwal, 1445 AH",
        latitude = 41.0,
        longitude = 28.9
    )

    private fun scheduleOn(date: LocalDate, isHanafiMode: Boolean = false) = PrayerSchedule(
        date = date,
        fajr = LocalTime.of(4, 30),
        sunrise = LocalTime.of(6, 5),
        dhuhr = LocalTime.of(12, 15),
        asr = LocalTime.of(15, 45),
        maghrib = LocalTime.of(18, 20),
        isha = LocalTime.of(19, 45),
        isHanafiMode = isHanafiMode
    )

    @Test
    fun `a schedule already cached for today with the current Hanafi mode is left alone`() = runTest {
        // Arrange: mockk<AthanTimeRepository> { every { ... } returns ... } builds a fake
        // repository whose observeSchedule() Flow emits exactly this cached schedule — same
        // pattern RoomAthanTimeRepositoryTest/HomeViewModelTest already use for Flow fakes.
        val today = LocalDate.now()
        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeSchedule() } returns flowOf(scheduleOn(today, isHanafiMode = false))
        }
        // Left completely unstubbed on purpose — if the use case ever called this when it
        // shouldn't, MockK would throw immediately since there's no stub to answer with
        val athanApiRepository = mockk<AthanApiRepository>()

        val useCase = RefreshPrayerScheduleUseCase(athanTimeRepository, athanApiRepository)

        // Act: RefreshPrayerScheduleUseCase declares `operator fun invoke(...)`, which is why
        // it can be called like a plain function (useCase(...)) instead of useCase.invoke(...)
        useCase(city = istanbul, today = today, isHanafiMode = false)

        // Assert: coVerify(exactly = 0) confirms this suspend function was never called at
        // all — the real assertion of this test, since a fresh cache means no network call
        coVerify(exactly = 0) { athanApiRepository.fetchNewPrayerSchedule(any(), any(), any(), any()) }
    }

    @Test
    fun `no cached schedule triggers a fetch and save, and reports that a fetch started`() = runTest {
        val today = LocalDate.now()
        val freshSchedule = scheduleOn(today)

        // Arrange: observeSchedule() emits null here — nothing cached yet (e.g. right after
        // onboarding's Location step saved a city but no schedule exists for it yet)
        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeSchedule() } returns flowOf(null)
            // saveCityWithSchedule is `suspend fun ... : Unit` — coEvery { ... } just Runs
            // stubs it to do nothing and return Unit, the same pattern HomeViewModelTest uses
            coEvery { saveCityWithSchedule(any(), any()) } just Runs
        }
        val athanApiRepository = mockk<AthanApiRepository> {
            coEvery {
                fetchNewPrayerSchedule(
                    latitude = istanbul.latitude,
                    longitude = istanbul.longitude,
                    date = today,
                    useHanafiAsr = false
                )
            } returns freshSchedule
        }

        val useCase = RefreshPrayerScheduleUseCase(athanTimeRepository, athanApiRepository)

        // Act: a plain local var captured by the onFetchStarted lambda — simplest way to
        // assert a zero-argument callback actually fired, without needing a mock just for it
        var fetchStartedFired = false
        useCase(
            city = istanbul,
            today = today,
            isHanafiMode = false,
            onFetchStarted = { fetchStartedFired = true }
        )

        // Assert: all three effects of a real fetch — the callback fired, the API was called,
        // and the result was saved back to the local repository
        assertTrue(fetchStartedFired)
        coVerify {
            athanApiRepository.fetchNewPrayerSchedule(
                latitude = istanbul.latitude,
                longitude = istanbul.longitude,
                date = today,
                useHanafiAsr = false
            )
            athanTimeRepository.saveCityWithSchedule(istanbul, freshSchedule)
        }
    }

    @Test
    fun `a schedule cached for a previous day is stale and gets refetched`() = runTest {
        val today = LocalDate.now()
        // Cached schedule's date is yesterday — same isHanafiMode as what's being asked for,
        // so date is the ONLY thing making this stale, isolating that half of the check
        val staleSchedule = scheduleOn(today.minusDays(1), isHanafiMode = false)
        val freshSchedule = scheduleOn(today, isHanafiMode = false)

        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeSchedule() } returns flowOf(staleSchedule)
            coEvery { saveCityWithSchedule(any(), any()) } just Runs
        }
        val athanApiRepository = mockk<AthanApiRepository> {
            coEvery { fetchNewPrayerSchedule(any(), any(), any(), any()) } returns freshSchedule
        }

        val useCase = RefreshPrayerScheduleUseCase(athanTimeRepository, athanApiRepository)
        useCase(city = istanbul, today = today, isHanafiMode = false)

        coVerify { athanTimeRepository.saveCityWithSchedule(istanbul, freshSchedule) }
    }

    @Test
    fun `a schedule cached today under the wrong Hanafi mode is stale and gets refetched`() = runTest {
        val today = LocalDate.now()
        // Cached schedule's date IS today — isHanafiMode is the ONLY mismatch here, isolating
        // the half of the check this whole feature was actually added for
        val staleSchedule = scheduleOn(today, isHanafiMode = false)
        val freshSchedule = scheduleOn(today, isHanafiMode = true)

        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeSchedule() } returns flowOf(staleSchedule)
            coEvery { saveCityWithSchedule(any(), any()) } just Runs
        }
        val athanApiRepository = mockk<AthanApiRepository> {
            coEvery { fetchNewPrayerSchedule(any(), any(), any(), any()) } returns freshSchedule
        }

        val useCase = RefreshPrayerScheduleUseCase(athanTimeRepository, athanApiRepository)
        // isHanafiMode = true here is the current DataStore setting — it disagrees with the
        // cached schedule's isHanafiMode = false, which is exactly what should trigger a refetch
        useCase(city = istanbul, today = today, isHanafiMode = true)

        coVerify { athanTimeRepository.saveCityWithSchedule(istanbul, freshSchedule) }
    }

    @Test
    fun `a fetch failure propagates as an exception instead of being swallowed`() = runTest {
        // Arrange: the use case deliberately has no try/catch of its own — this test proves
        // that design choice, since it's each caller's job to decide how to handle a failure
        // (HomeViewModel surfaces it on homeState.error; the worker will return Result.retry())
        val today = LocalDate.now()
        val athanTimeRepository = mockk<AthanTimeRepository> {
            every { observeSchedule() } returns flowOf(null)
        }
        val athanApiRepository = mockk<AthanApiRepository> {
            coEvery { fetchNewPrayerSchedule(any(), any(), any(), any()) } throws RuntimeException("network down")
        }
        val useCase = RefreshPrayerScheduleUseCase(athanTimeRepository, athanApiRepository)

        // Act + Assert: a plain try/catch, not JUnit's assertThrows — assertThrows takes a
        // plain (non-suspend) lambda, but useCase(...) is a suspend call and this whole test
        // body already runs inside runTest's suspend block
        var caught: Exception? = null
        try {
            useCase(city = istanbul, today = today, isHanafiMode = false)
        } catch (e: Exception) {
            caught = e
        }

        assertTrue(caught is RuntimeException)
        assertEquals("network down", caught?.message)
    }
}