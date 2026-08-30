package com.example.athanly.athanTime.remote.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Mirrors Aladhan's /v1/timings/{date} response shape (verified against a live call) so
// kotlinx.serialization can parse it directly — only "data.timings" is modeled since that's
// all the repository needs to build a PrayerSchedule; "date"/"meta" are left out for now.
@Serializable
data class AlAdhanTimingsResponseDto(
    val data: AlAdhanDataDto
)

@Serializable
data class AlAdhanDataDto(
    val timings: AlAdhanTimingsDto
)

// Aladhan returns capitalized keys (e.g. "Fajr") — @SerialName keeps our fields
// lowerCamelCase while still matching the wire format.
@Serializable
data class AlAdhanTimingsDto(
    @SerialName("Fajr") val fajr: String,
    @SerialName("Sunrise") val sunrise: String,
    @SerialName("Dhuhr") val dhuhr: String,
    @SerialName("Asr") val asr: String,
    @SerialName("Maghrib") val maghrib: String,
    @SerialName("Isha") val isha: String
)