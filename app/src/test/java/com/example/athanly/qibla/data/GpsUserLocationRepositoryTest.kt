package com.example.athanly.qibla.data

import com.example.athanly.location.domain.repository.LocationProvider
import com.example.athanly.qibla.local.data.GpsUserLocationRepository
import com.example.athanly.qibla.local.domain.modelData.Coordinates
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class GpsUserLocationRepositoryTest {
    @Test
    fun `getCurrentLocation returns the coordinates LocationProvider returns`() = runTest {
        // mockk<LocationProvider>() fakes the interface so this test never touches real
        // GPS hardware — we control exactly what "the device's location" is for this test.
        val locationProvider = mockk<LocationProvider>()

        // coEvery {} stubs a *suspend* function (plain `every` is for non-suspend calls).
        // This says: whenever getCurrentCoordinates() is called on our fake, return this
        // fixed coordinate instead of doing a real GPS lookup.
        coEvery { locationProvider.getCurrentCoordinates() } returns Coordinates(
            latitude = 30.0444,
            longitude = 31.2357
        )

        val repository = GpsUserLocationRepository(locationProvider)

        // A one-shot suspend call just returns its value, so no Turbine is needed —
        // runTest lets us call it directly and assert on the result.
        val coordinates = repository.getCurrentLocation()

        assertEquals(Coordinates(latitude = 30.0444, longitude = 31.2357), coordinates)
    }

    @Test
    fun `getCurrentLocation throws when LocationProvider has no fix`() = runTest {
        val locationProvider = mockk<LocationProvider>()

        // Stubbing a null return — this is the "no GPS fix yet" case (permission denied,
        // location off, or just no result back in time).
        coEvery { locationProvider.getCurrentCoordinates() } returns null

        val repository = GpsUserLocationRepository(locationProvider)

        // try/catch + fail() instead of assertThrows: assertThrows' lambda isn't a suspend
        // block, so it can't call getCurrentLocation(). Throwing (rather than returning
        // null) is what lets QiblaViewModel's existing try/catch surface e.message as an
        // error instead of silently showing nothing.
        try {
            repository.getCurrentLocation()
            fail("Expected an IllegalStateException when there is no GPS fix")
        } catch (e: IllegalStateException) {
            assertEquals("No GPS fix available", e.message)
        }
    }

    @Test
    fun `hasLocationPermission returns true when LocationProvider has permission`() {
        val locationProvider = mockk<LocationProvider>()

        // every {} (not coEvery) stubs a *non-suspend* function — hasLocationPermission()
        // is a plain synchronous check, unlike getCurrentCoordinates() above.
        every { locationProvider.hasLocationPermission() } returns true

        val repository = GpsUserLocationRepository(locationProvider)

        // This method just forwards the result — the test only needs to prove the
        // delegation happens correctly, not re-test LocationProviderImpl's own permission logic.
        assertTrue(repository.hasLocationPermission())
    }

    @Test
    fun `hasLocationPermission returns false when LocationProvider has no permission`() {
        val locationProvider = mockk<LocationProvider>()
        every { locationProvider.hasLocationPermission() } returns false

        val repository = GpsUserLocationRepository(locationProvider)

        assertFalse(repository.hasLocationPermission())
    }
}