package com.example.athanly.qibla.domain.local.repository

import kotlinx.coroutines.flow.Flow

// Abstraction over the device's current compass heading (degrees from magnetic north),
// backed by the phone's rotation-vector sensor — see SensorCompassRepository.
interface CompassRepository {
    fun observedHeadingDegrees(): Flow<Float>
}