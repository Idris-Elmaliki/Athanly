package com.example.athanly.qibla.local.domain.repository

import com.example.athanly.qibla.local.domain.modelData.Coordinates
import kotlinx.coroutines.flow.Flow

// Abstraction over "where is the user right now" — mock-backed today (MockUserLocationRepository),
// swappable for a real GPS/City-backed implementation later without QiblaViewModel changing at all.
interface UserLocationRepository {
    fun observedLocation(): Flow<Coordinates>
}