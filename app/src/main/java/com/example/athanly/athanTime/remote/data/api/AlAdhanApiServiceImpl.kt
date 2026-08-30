package com.example.athanly.athanTime.remote.data.api

import com.example.athanly.athanTime.remote.data.dto.AlAdhanTimingsResponseDto
import com.example.athanly.athanTime.remote.domain.api.AlAdhanApiService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AlAdhanApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : AlAdhanApiService {
    override suspend fun getTimings(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        school: Int
    ): AlAdhanTimingsResponseDto {
        return httpClient.get("$BASE_URL/v1/timings/${date.format(DATE_FORMATTER)}") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("method", TUNISIA_METHOD)
            parameter("school", school)
        }.body()
    }

    private companion object {
        const val BASE_URL = "https://api.aladhan.com"

        // Tunisia calculation method — fixed 18deg Fajr/Isha, matches the app's Fajr requirement
        const val TUNISIA_METHOD = 18
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }
}