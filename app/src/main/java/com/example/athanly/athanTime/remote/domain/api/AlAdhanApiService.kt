package com.example.athanly.athanTime.remote.domain.api

import com.example.athanly.athanTime.remote.data.dto.AlAdhanTimingsResponseDto
import java.time.LocalDate

// The Tunisia method (18° Fajr/Isha) is fixed for this app, so it isn't a parameter here —
// only what actually varies per call (date, location, Hanafi/Shafi school) is exposed.
interface AlAdhanApiService {
    suspend fun getTimings(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        school: Int
    ): AlAdhanTimingsResponseDto
}