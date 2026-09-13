package com.example.athanly.qibla.remote.data.dto

import kotlinx.serialization.Serializable

// Mirrors Aladhan's /v1/qibla/{latitude}/{longitude} response shape (verified against a
// live call: {"code":200,"status":"OK","data":{"latitude":..,"longitude":..,"direction":..}}).
// Only "data.direction" is modeled — "code"/"status" are just call-status noise, and
// "data.latitude"/"data.longitude" just echo back what we sent, so neither is needed.
@Serializable
data class AlAdhanQiblaResponseDto(
    val data: AlAdhanQiblaDataDto
)

@Serializable
data class AlAdhanQiblaDataDto(
    val direction: Double
)