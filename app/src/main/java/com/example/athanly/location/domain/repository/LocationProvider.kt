package com.example.athanly.location.domain.repository

import com.example.athanly.athanTime.local.domain.modelData.City

// Wraps FusedLocationProviderClient + reverse geocoding behind a domain-friendly interface —
// the onboarding Location screen never touches Google Play Services types directly.
interface LocationProvider {

    // Null means no fix was obtained (permission denied, location off, or the device
    // couldn't resolve one in time) — the caller falls back to PresetCityPicker, it's not
    // an exceptional case worth throwing over.
    suspend fun getCurrentCity(): City?
}