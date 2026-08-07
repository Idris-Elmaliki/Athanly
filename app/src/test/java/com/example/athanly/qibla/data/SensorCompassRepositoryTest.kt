package com.example.athanly.qibla.data

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI

class SensorCompassRepositoryTest {

    @Test
    fun `headingDegreesFrom normalizes a negative azimuth into the 0-360 range`() {
        // getOrientation() returns azimuth in radians, range -pi..pi, where 0 = magnetic
        // north. -pi/2 is 90 degrees counterclockwise from north, i.e. facing west,
        // which as a compass heading should read 270 degrees — same negative-value
        // wraparound problem as bearingTo's atan2 result, solved the same way.
        val azimuthRadians = (-PI / 2).toFloat()

        val headingDegrees = SensorCompassRepository.headingDegreesFrom(azimuthRadians)

        assertEquals(270f, headingDegrees, 0.01f)
    }
}