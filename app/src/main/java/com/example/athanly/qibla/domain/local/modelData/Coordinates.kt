package com.example.athanly.qibla.domain.local.modelData

// Plain lat/lng pair — kept separate from City so the Qibla calculator works
// against any coordinate source (mock today, real GPS/City later) without caring where it came from.
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)