package com.example.athanly.athanTime.local.domain.modelData

// The user's single current/chosen location. No id — there's only ever one, so nothing
// needs to address it. lat/lng default to 0.0 until the Location onboarding step or the
// Room row-mapper stamps in a real fix.
data class City(
    val name: String,
    val hijriDate: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)