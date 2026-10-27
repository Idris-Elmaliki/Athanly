package com.example.athanly.qibla.local.domain.repository

import com.example.athanly.qibla.local.domain.modelData.Coordinates

// Abstraction over "where is the user right now" — mock-backed today (MockUserLocationRepository),
// swappable for a real GPS/City-backed implementation later without QiblaViewModel changing at all.
interface UserLocationRepository {
    // One-shot, not a stream — the user is stationary while using Qibla. Throws when there's
    // no fix, so the caller's try/catch can surface the failure.
    suspend fun getCurrentLocation(): Coordinates

    // "Can I get the user's location" intrinsically includes "do I have permission to try" —
    // exposed here so QiblaViewModel can check it directly instead of inferring it from
    // getCurrentLocation()'s generic "no fix" failure.
    fun hasLocationPermission(): Boolean

    // Permission granted doesn't mean the device's location toggle is on — checked separately
    // so QiblaViewModel can tell "app not allowed" apart from "location switched off".
    fun isLocationEnabled(): Boolean
}