package com.example.athanly.location.domain.repository

import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.qibla.local.domain.modelData.Coordinates

// Wraps FusedLocationProviderClient + reverse geocoding behind a domain-friendly interface —
// the onboarding Location screen never touches Google Play Services types directly.
interface LocationProvider {

    // Null means no fix was obtained (permission denied, location off, or the device
    // couldn't resolve one in time) — the caller falls back to PresetCityPicker, it's not
    // an exceptional case worth throwing over.
    suspend fun getCurrentCity(): City?

    // Same GPS fix as getCurrentCity(), minus the reverse-geocode step — Qibla only needs
    // a lat/lng to do its bearing math, so it skips work getCurrentCity() would waste on it.
    suspend fun getCurrentCoordinates(): Coordinates?
}