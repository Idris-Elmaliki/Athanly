package com.example.athanly.qibla.userInfoCalculations

import com.example.athanly.qibla.local.domain.modelData.Coordinates
import com.example.athanly.qibla.local.domain.modelData.enums.CardinalDirection
import com.example.athanly.userInfoCalculations.QiblaCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class QiblaCalculatorTest {

    // `@Test` marks this function as a test case — JUnit4 discovers and runs every
    // function annotated this way when the test class executes.
    @Test
    fun `distanceTo returns a quarter of Earth's circumference for two points 90 degrees of latitude apart`() {
        // Two points on the same meridian (longitude 0), 90 degrees of latitude apart:
        // the equator (0,0) and the North Pole (90,0). The great-circle path between
        // them is exactly a quarter of Earth's circumference — easy to verify by hand,
        // independent of the Kaaba-specific coordinates the real feature will use.
        val equator = Coordinates(latitude = 0.0, longitude = 0.0)
        val northPole = Coordinates(latitude = 90.0, longitude = 0.0)

        // Earth's mean radius is ~6371 km, so a quarter of its circumference is
        // (2 * pi * 6371) / 4 ≈ 10007.5 km.
        val expectedDistanceKm = 10007.5

        val actualDistanceKm = QiblaCalculator.distanceTo(from = equator, to = northPole)

        // assertEquals(expected, actual, delta) — the third argument is a tolerance,
        // required for Double comparisons since floating-point math is never exact.
        // 1.0 km of slack is generous enough to absorb rounding in Earth's radius constant.
        assertEquals(expectedDistanceKm, actualDistanceKm, 1.0)
    }

    @Test
    fun `distanceToKaaba returns zero when already at the Kaaba's coordinates`() {
        // Cheap, hand-verifiable check that distanceToKaaba wires the fixed Kaaba
        // constant into distanceTo correctly, without re-deriving a real-world distance
        // by hand — the formula itself is already covered by the test above.
        val atKaaba = Coordinates(
            latitude = QiblaCalculator.KAABA_LATITUDE,
            longitude = QiblaCalculator.KAABA_LONGITUDE
        )

        assertEquals(0.0, QiblaCalculator.distanceToKaaba(from = atKaaba), 0.01)
    }

    @Test
    fun `kmToMiles converts using the standard km-to-mile factor`() {
        val expectedMiles = 62.1371

        val actualMiles = QiblaCalculator.kmToMiles(km = 100.0)

        assertEquals(expectedMiles, actualMiles, 0.001)
    }

    @Test
    fun `cardinalDirectionFor returns NE for 65 degrees`() {
        // 65 lands inside the NE octant of an 8-point compass (each octant spans 45
        // degrees, centered on its direction — NE is centered on 45, so its range is
        // 22.5..67.5). Matches the "65 degrees NE" example from the design.
        assertEquals(CardinalDirection.NE, QiblaCalculator.cardinalDirectionFor(bearingDegrees = 65.0))
    }

    @Test
    fun `cardinalDirectionFor returns N for a bearing that wraps past 360`() {
        // 350 degrees is close to due north (360/0) but still counts as N since it's
        // within 22.5 degrees of 0 going the "short way" around — this is the case
        // that actually exercises the wraparound, unlike a bearing already near 0.
        assertEquals(CardinalDirection.N, QiblaCalculator.cardinalDirectionFor(bearingDegrees = 350.0))
    }
}