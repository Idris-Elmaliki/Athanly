package com.example.athanly.qibla.data

import com.example.athanly.qibla.local.data.SensorCompassRepository
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

    @Test
    fun `trueHeadingFrom adds the declination to a magnetic heading`() {
        // Declination = how far magnetic north sits east (+) of true north at this spot.
        // Facing 90 degrees from magnetic north with +10 declination means 100 degrees
        // from true north. assertEquals(expected, actual, delta): the delta is the allowed
        // float rounding error, since floats are never compared exactly.
        val magneticHeadingDegrees = 90f
        val declinationDegrees = 10f

        val trueHeadingDegrees = SensorCompassRepository.trueHeadingFrom(
            magneticHeadingDegrees = magneticHeadingDegrees,
            declinationDegrees = declinationDegrees
        )

        assertEquals(100f, trueHeadingDegrees, 0.01f)
    }

    @Test
    fun `trueHeadingFrom wraps results back into the 0-360 range`() {
        // Adding declination can push past 360 (350 + 20 = 370 -> 10) or below 0
        // (5 - 10 = -5 -> 355). Two cases in one test, since both are the same wraparound rule.
        val pastFullCircle = SensorCompassRepository.trueHeadingFrom(
            magneticHeadingDegrees = 350f,
            declinationDegrees = 20f
        )
        val belowZero = SensorCompassRepository.trueHeadingFrom(
            magneticHeadingDegrees = 5f,
            declinationDegrees = -10f
        )

        assertEquals(10f, pastFullCircle, 0.01f)
        assertEquals(355f, belowZero, 0.01f)
    }
}