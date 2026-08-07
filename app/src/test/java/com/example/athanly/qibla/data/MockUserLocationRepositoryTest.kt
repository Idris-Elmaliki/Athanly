package com.example.athanly.qibla.data

import app.cash.turbine.test
import com.example.athanly.qibla.domain.local.modelData.Coordinates
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MockUserLocationRepositoryTest {

    @Test
    fun `observedLocation emits a fixed New York coordinate`() = runTest {
        // .test {} (Turbine) subscribes to the Flow and lets us step through its
        // emissions one at a time via awaitItem(), instead of manually collecting.
        MockUserLocationRepository().observedLocation().test {
            val coordinates = awaitItem()
            assertEquals(Coordinates(latitude = 40.7128, longitude = -74.0060), coordinates)

            // The mock emits once and finishes (it's a fixed value, not a live stream),
            // so Turbine expects the Flow to signal completion next.
            awaitComplete()
        }
    }
}
