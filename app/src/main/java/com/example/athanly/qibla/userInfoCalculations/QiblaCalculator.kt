package com.example.athanly.qibla.userInfoCalculations

import com.example.athanly.qibla.local.domain.modelData.Coordinates
import com.example.athanly.qibla.local.domain.modelData.enums.CardinalDirection
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

// `object` = a singleton — there's no per-instance state here, just pure functions
// operating on whatever Coordinates are passed in, so a class instance would add
// nothing. This is why the top-level directory is userInfoCalculations, not a class
// living inside domain/ — it's shared math, not a data model.
object QiblaCalculator {

    // Earth's mean radius in km. `const val` (vs plain `val`) means this is a true
    // compile-time constant — usable here because it's a primitive literal, not
    // something computed at runtime.
    private const val EARTH_RADIUS_KM = 6371.0
    private const val KM_TO_MILES_FACTOR = 0.621371

    const val KAABA_LATITUDE = 21.4225
    const val KAABA_LONGITUDE = 39.8262
    
    private val kaaba = Coordinates(
        latitude = KAABA_LATITUDE, 
        longitude = KAABA_LONGITUDE
    )

    // Haversine formula — great-circle distance between two points on a sphere.
    fun distanceTo(from: Coordinates, to: Coordinates): Double {
        // kotlin.math's sin/cos/atan2 all expect radians, but Coordinates stores
        // degrees, so every value goes through Math.toRadians first.
        val fromLatitudeRadians = Math.toRadians(from.latitude)
        val toLatitudeRadians = Math.toRadians(to.latitude)
        val latitudeDeltaRadians = Math.toRadians(to.latitude - from.latitude)
        val longitudeDeltaRadians = Math.toRadians(to.longitude - from.longitude)

        // Combines the lat/lon separation into one number via sin²(θ/2) (the
        // "haversine" function) — chosen historically because it stays accurate for
        // tiny angles, unlike other trig substitutions. The cos(lat)*cos(lat) factor
        // shrinks the longitude term near the poles, where lines of longitude
        // converge and the same longitude delta covers less real ground.
        val squaredHalfChordLength = sin(latitudeDeltaRadians / 2).pow(2) +
            cos(fromLatitudeRadians) * cos(toLatitudeRadians) * sin(longitudeDeltaRadians / 2).pow(2)

        // Converts that combined value into the actual angle (radians) between the
        // two points as seen from Earth's center. atan2(y, x) is the two-argument
        // arctangent — unlike atan(y/x), it doesn't lose the sign information division
        // throws away, so it's stable/accurate across the whole range instead of
        // breaking down (like asin would) for points near opposite sides of the globe.
        val angularDistanceRadians = 2 * atan2(sqrt(squaredHalfChordLength), sqrt(1 - squaredHalfChordLength))

        // Arc length = radius * angle (same relationship as for any circle).
        return EARTH_RADIUS_KM * angularDistanceRadians
    }

    fun distanceToKaaba(from: Coordinates): Double = distanceTo(from = from, to = kaaba)

    // Bearing to the Kaaba now comes from QiblaApiRepository (Aladhan's /v1/qibla
    // endpoint) instead of being computed here — bearingTo/bearingToKaaba were removed.

    fun kmToMiles(km: Double): Double = km * KM_TO_MILES_FACTOR

    // Splits the compass into 8 equal 45-degree slices, each centered on its
    // direction (e.g. NE covers 22.5..67.5). Dividing by 45 and rounding finds which
    // slice a bearing falls in; CardinalDirection.entries is declared in N, NE, E,
    // SE, S, SW, W, NW order specifically so that slice index lines up with it
    // directly. The final % 8 wraps slice 8 (from a bearing near 360) back to 0 (N).
    fun cardinalDirectionFor(bearingDegrees: Double): CardinalDirection {
        val sliceIndex = (bearingDegrees / 45.0).roundToInt() % 8
        return CardinalDirection.entries[sliceIndex]
    }
}
