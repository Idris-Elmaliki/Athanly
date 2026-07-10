package com.example.athanly.athanTime.local.domain.modelData

// One saved/swipeable city on the Home screen. lat/lng default to 0.0 for now — real
// geocoding comes later when location permissions + search are built.
data class City(
    val id: String,
    val name: String,
    val hijriDate: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)