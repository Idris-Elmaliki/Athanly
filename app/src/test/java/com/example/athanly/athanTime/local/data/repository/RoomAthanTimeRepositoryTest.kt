package com.example.athanly.athanTime.local.data.repository

import app.cash.turbine.test
import com.example.athanly.athanTime.local.data.dao.AthanTimeDao
import com.example.athanly.athanTime.local.data.entity.CityEntity
import com.example.athanly.athanTime.local.data.entity.PrayerScheduleEntity
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.domain.PrayerSchedule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class RoomAthanTimeRepositoryTest {
    // mockk<T>() creates a fake implementation of the DAO interface at test time — every
    // method returns nothing/throws by default until we explicitly stub it per-test with
    // `every { ... } returns ...` (for plain calls) or `coEvery { ... } returns ...` (for
    // suspend calls). This lets us test the repository's mapping/delegation logic without
    // touching a real SQLite database.
    private lateinit var dao: AthanTimeDao

    // The class under test — created fresh before each test so no state leaks between tests.
    private lateinit var repository: RoomAthanTimeRepository

    // JUnit4's @Before runs this before every @Test method in this class.
    @Before
    fun setUp() {
        dao = mockk()
        repository = RoomAthanTimeRepository(dao)
    }

    // runTest {} (kotlinx-coroutines-test) runs the block on a TestDispatcher — needed because
    // collecting a Flow requires a coroutine, but we don't want the test waiting on a real
    // clock/dispatcher.
    @Test
    fun `observeCity emits the mapped domain City when the DAO emits a CityEntity`() = runTest {
        // Arrange: the domain City has no id (there's only ever one) — the DAO row's id is
        // a Room-only concern this repository hides, so it never appears in expectedCity.
        val cityEntity = CityEntity(
            id = "current_location",
            name = "Istanbul",
            hijriDate = "12 Rabi' al-Awwal, 1445 AH",
            latitude = 41.0082,
            longitude = 28.9784
        )
        every { dao.observeCity() } returns flowOf(cityEntity)

        val expectedCity = City(
            name = "Istanbul",
            hijriDate = "12 Rabi' al-Awwal, 1445 AH",
            latitude = 41.0082,
            longitude = 28.9784
        )

        // Act + Assert: Turbine's `.test { }` collects the Flow inside the block. awaitItem()
        // suspends until the next emission arrives and returns it; awaitComplete() asserts the
        // Flow finishes with no further emissions — both fail the test if they don't happen.
        repository.observeCity().test {
            assert(awaitItem() == expectedCity)
            awaitComplete()
        }
    }

    @Test
    fun `observeCity emits null when no city has been saved yet`() = runTest {
        // Arrange: no city saved yet is a normal, expected state (e.g. before onboarding's
        // Location step runs) — not an error — so the DAO just emits null, not an exception.
        every { dao.observeCity() } returns flowOf(null)

        repository.observeCity().test {
            assert(awaitItem() == null)
            awaitComplete()
        }
    }

    @Test
    fun `observeSchedule emits the mapped domain PrayerSchedule when one is cached`() = runTest {
        // Arrange: the entity stores times/date as ISO-8601 Strings (per the "Room columns are
        // String, converted explicitly" decision) — the repository's mapper is what turns
        // these back into java.time types for the domain model.
        val scheduleEntity = PrayerScheduleEntity(
            cityId = "current_location",
            date = "2026-08-29",
            fajr = "04:30:00",
            sunrise = "06:05:00",
            dhuhr = "12:15:00",
            asr = "15:45:00",
            maghrib = "18:20:00",
            isha = "19:45:00"
        )
        every { dao.observeSchedule() } returns flowOf(scheduleEntity)

        val expectedSchedule = PrayerSchedule(
            date = LocalDate.of(2026, 8, 29),
            fajr = LocalTime.of(4, 30),
            sunrise = LocalTime.of(6, 5),
            dhuhr = LocalTime.of(12, 15),
            asr = LocalTime.of(15, 45),
            maghrib = LocalTime.of(18, 20),
            isha = LocalTime.of(19, 45)
        )

        repository.observeSchedule().test {
            assert(awaitItem() == expectedSchedule)
            awaitComplete()
        }
    }

    @Test
    fun `observeSchedule emits null when no schedule is cached`() = runTest {
        // Arrange: a city can exist with no schedule cached yet (e.g. onboarding just saved
        // the city but the Athan API hasn't been called yet) — the DAO's nullable return type
        // models that directly, so the mapper must pass null through rather than crashing on it.
        every { dao.observeSchedule() } returns flowOf(null)

        repository.observeSchedule().test {
            assert(awaitItem() == null)
            awaitComplete()
        }
    }

    @Test
    fun `saveCity delegates to dao upsertCity with the mapped entity`() = runTest {
        // Arrange: `coEvery` (not `every`) is required here because upsertCity is a suspend
        // function on the DAO. `just Runs` stubs it to complete successfully with no return
        // value (Unit) and no real work — we only care that it was CALLED correctly.
        val city = City(
            name = "Istanbul",
            hijriDate = "12 Rabi' al-Awwal, 1445 AH",
            latitude = 41.0082,
            longitude = 28.9784
        )
        val expectedCityEntity = CityEntity(
            id = "current_location",
            name = "Istanbul",
            hijriDate = "12 Rabi' al-Awwal, 1445 AH",
            latitude = 41.0082,
            longitude = 28.9784
        )
        coEvery { dao.upsertCity(any()) } just Runs

        // Act
        repository.saveCity(city)

        // Assert: `coVerify` checks the suspend function was actually called with these exact
        // argument values — since CityEntity is a data class, MockK compares it by structural
        // equals(), so this fails if the mapping (including the hardcoded id) is wrong.
        coVerify { dao.upsertCity(expectedCityEntity) }
    }

    @Test
    fun `saveCityWithSchedule delegates to dao upsertCityWithSchedule with the mapped entities`() = runTest {
        val city = City(
            name = "Istanbul",
            hijriDate = "12 Rabi' al-Awwal, 1445 AH",
            latitude = 41.0082,
            longitude = 28.9784
        )
        val schedule = PrayerSchedule(
            date = LocalDate.of(2026, 8, 29),
            fajr = LocalTime.of(4, 30),
            sunrise = LocalTime.of(6, 5),
            dhuhr = LocalTime.of(12, 15),
            asr = LocalTime.of(15, 45),
            maghrib = LocalTime.of(18, 20),
            isha = LocalTime.of(19, 45)
        )
        val expectedCityEntity = CityEntity(
            id = "current_location",
            name = "Istanbul",
            hijriDate = "12 Rabi' al-Awwal, 1445 AH",
            latitude = 41.0082,
            longitude = 28.9784
        )
        val expectedScheduleEntity = PrayerScheduleEntity(
            cityId = "current_location",
            date = "2026-08-29",
            fajr = "04:30:00",
            sunrise = "06:05:00",
            dhuhr = "12:15:00",
            asr = "15:45:00",
            maghrib = "18:20:00",
            isha = "19:45:00"
        )
        coEvery { dao.upsertCityWithSchedule(any(), any()) } just Runs

        // Act
        repository.saveCityWithSchedule(city, schedule)

        // Assert
        coVerify { dao.upsertCityWithSchedule(expectedCityEntity, expectedScheduleEntity) }
    }
}