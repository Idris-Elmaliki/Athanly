package com.example.athanly.qibla.remote.domain.repository

// Returns just the bearing in degrees — the domain concept QiblaViewModel actually wants,
// keeping the DTO's response shape (data.direction, plus code/status noise) out of callers.
interface QiblaApiRepository {
    suspend fun fetchQiblaBearing(
        latitude: Double,
        longitude: Double
    ): Double
}