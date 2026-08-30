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
    fun `observeCities emits mapped domain Cities when DAO emits CityEntities`() = runTest {
        // Arrange: stub the DAO's non-suspend Flow-returning function with `every { } returns`
        // (use `coEvery` instead for suspend functions — observeCities() isn't suspend, so
        // `every` is correct here). flowOf(...) builds a Flow that emits this one list, then
        // completes.
        val cityEntity = CityEntity(
            id = "istanbul",
            name = "Istanbul",
            hijriDate = "12 Rabi' al-Awwal, 1445 AH",
            latitude = 41.0082,
            longitude = 28.9784
        )
        every { dao.observeCities() } returns flowOf(listOf(cityEntity))

        val expectedCity = City(
            id = "istanbul",
            name = "Istanbul",
            hijriDate = "12 Rabi' al-Awwal, 1445 AH",
            latitude = 41.0082,
            longitude = 28.9784
        )

        // Act + Assert: Turbine's `.test { }` collects the Flow inside the block. awaitItem()
        // suspends until the next emission arrives and returns it; awaitComplete() asserts the
        // Flow finishes with no further emissions — both fail the test if they don't happen.
        repository.observeCities().test {
            assert(awaitItem() == listOf(expectedCity))
            awaitComplete()
        }
    }

    @Test
    fun `observeCities emits empty list when DAO has no cached cities`() = runTest {
        // Arrange: an empty cache is a normal, expected state (e.g. before any city has ever
        // been saved) — not an error — so the DAO just emits an empty list, not null/an
        // exception.
        every { dao.observeCities() } returns flowOf(emptyList())

        repository.observeCities().test {
            assert(awaitItem() == emptyList<City>())
            awaitComplete()
        }
    }

    @Test
    fun `observeSchedule emits mapped domain PrayerSchedule for an existing cityId`() = runTest {
        // Arrange: the entity stores times/date as ISO-8601 Strings (per the "Room columns are
        // String, converted explicitly" decision) — the repository's mapper is what turns
        // these back into java.time types for the domain model.
        val scheduleEntity = PrayerScheduleEntity(
            cityId = "istanbul",
            date = "2026-08-29",
            fajr = "04:30:00",
            sunrise = "06:05:00",
            dhuhr = "12:15:00",
            asr = "15:45:00",
            maghrib = "18:20:00",
            isha = "19:45:00"
        )
        every { dao.observeScheduleForCity("istanbul") } returns flowOf(scheduleEntity)

        val expectedSchedule = PrayerSchedule(
            date = LocalDate.of(2026, 8, 29),
            fajr = LocalTime.of(4, 30),
            sunrise = LocalTime.of(6, 5),
            dhuhr = LocalTime.of(12, 15),
            asr = LocalTime.of(15, 45),
            maghrib = LocalTime.of(18, 20),
            isha = LocalTime.of(19, 45)
        )

        repository.observeSchedule("istanbul").test {
            assert(awaitItem() == expectedSchedule)
            awaitComplete()
        }
    }

    @Test
    fun `observeSchedule emits null when no schedule is cached for that cityId`() = runTest {
        // Arrange: a city can exist with no schedule cached yet (e.g. the city was just added
        // but the Athan API hasn't been called for it) — the DAO's nullable return type models
        // that directly, so the mapper must pass null through rather than crashing on it.
        every { dao.observeScheduleForCity("new-york") } returns flowOf(null)

        repository.observeSchedule("new-york").test {
            assert(awaitItem() == null)
            awaitComplete()
        }
    }

    @Test
    fun `saveCityWithSchedule delegates to dao upsertCityWithSchedule with the mapped entities`() = runTest {
        // Arrange: `coEvery` (not `every`) is required here because upsertCityWithSchedule is a
        // suspend function on the DAO. `just Runs` stubs it to complete successfully with no
        // return value (Unit) and no real work — we only care that it was CALLED correctly.
        val city = City(
            id = "istanbul",
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
            id = "istanbul",
            name = "Istanbul",
            hijriDate = "12 Rabi' al-Awwal, 1445 AH",
            latitude = 41.0082,
            longitude = 28.9784
        )
        val expectedScheduleEntity = PrayerScheduleEntity(
            cityId = "istanbul",
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

        // Assert: `coVerify` checks the suspend function was actually called with these exact
        // argument values — since both entity types are data classes, MockK compares them by
        // structural equals(), so this fails if the mapping produced the wrong fields.
        coVerify { dao.upsertCityWithSchedule(expectedCityEntity, expectedScheduleEntity) }
    }

    @Test
    fun `deleteCity delegates to dao deleteCity with the given cityId`() = runTest {
        // No mapping involved here — cityId is a plain String passed straight through, so
        // this test only needs to confirm the repository doesn't drop or alter the argument.
        coEvery { dao.deleteCity("istanbul") } just Runs

        repository.deleteCity("istanbul")

        coVerify { dao.deleteCity("istanbul") }
    }

    // Known gap: PrayerScheduleEntity's ON DELETE CASCADE (deleting a city removes its
    // schedule) is enforced by real SQLite, not by this mocked DAO — mocking never touches
    // actual SQL, so that behavior isn't covered here. Revisit with an in-memory-Room
    // instrumented test if/when that infra is worth adding to the project.
}