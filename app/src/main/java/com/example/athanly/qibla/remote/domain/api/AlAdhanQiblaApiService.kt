package com.example.athanly.qibla.remote.domain.api

import com.example.athanly.qibla.remote.data.dto.AlAdhanQiblaResponseDto

// Mirrors AlAdhanApiService's pattern: a thin domain-facing contract, no HTTP/Ktor types
// leaking through it.
interface AlAdhanQiblaApiService {
    suspend fun getQiblaDirection(
        latitude: Double,
        longitude: Double
    ): AlAdhanQiblaResponseDto
}