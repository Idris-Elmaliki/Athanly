package com.example.athanly.qibla.remote.data.dto

import kotlinx.serialization.Serializable

// Mirrors Aladhan's /v1/qibla/{latitude}/{longitude} response shape (verified against a
// live call: {
//      "code":200,
//      "status":"OK",
//      "data":{
//          "latitude":..,
//          "longitude":..,
//          "direction":..
//      }
// }.
// Only "data.direction" is modeled — "code"/"status" are just call-status noise, and
// "data.latitude"/"data.longitude" just echo back what we sent, so neither is needed.

// Two classes because the JSON has two levels of { }: the outer object (this class) and the
// nested "data" object (AlAdhanQiblaDataDto). The class nesting must match the JSON nesting,
// or parsing fails on a missing field.
@Serializable
data class AlAdhanQiblaResponseDto(
    // The outer { "data": ... } — the key name must match the JSON exactly.
    val data: AlAdhanQiblaDataDto
)

// The inner { "direction": ... } object that "data" holds.
@Serializable
data class AlAdhanQiblaDataDto(
    // Qibla bearing in degrees from true north, calculated by the API from our lat/lng.
    val direction: Double
)