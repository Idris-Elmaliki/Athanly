package com.example.athanly.location.domain.modelData

import com.example.athanly.athanTime.local.domain.modelData.City

// Shown by PresetCityPicker when the user denies/skips the location permission on
// onboarding's Location step. hijriDate is left "" — nothing in the codebase computes a
// real Hijri date yet (it's a placeholder everywhere else too), so this doesn't invent one;
// whatever eventually fills it in for the GPS path should fill it in here too.
val presetCities = listOf(
    City(name = "New York", hijriDate = "", latitude = 40.7128, longitude = -74.0060),
    City(name = "London", hijriDate = "", latitude = 51.5074, longitude = -0.1278),
    City(name = "Toronto", hijriDate = "", latitude = 43.6532, longitude = -79.3832),
    City(name = "Sydney", hijriDate = "", latitude = -33.8688, longitude = 151.2093),
    City(name = "Mecca", hijriDate = "", latitude = 21.4225, longitude = 39.8262),
    City(name = "Medina", hijriDate = "", latitude = 24.5247, longitude = 39.5692),
    City(name = "Jerusalem", hijriDate = "", latitude = 31.7683, longitude = 35.2137),
    City(name = "Istanbul", hijriDate = "", latitude = 41.0082, longitude = 28.9784),
    City(name = "Cairo", hijriDate = "", latitude = 30.0444, longitude = 31.2357),
    City(name = "Dubai", hijriDate = "", latitude = 25.2048, longitude = 55.2708),
    City(name = "Riyadh", hijriDate = "", latitude = 24.7136, longitude = 46.6753),
    City(name = "Amman", hijriDate = "", latitude = 31.9454, longitude = 35.9284),
    City(name = "Baghdad", hijriDate = "", latitude = 33.3152, longitude = 44.3661)
)