package com.example.athanly.qibla.remote.data.api

import com.example.athanly.qibla.remote.data.dto.AlAdhanQiblaResponseDto
import com.example.athanly.qibla.remote.domain.api.AlAdhanQiblaApiService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class AlAdhanQiblaApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : AlAdhanQiblaApiService {
    // Unlike /v1/timings, this endpoint takes latitude/longitude as path segments, not
    // query parameters.
    override suspend fun getQiblaDirection(
        latitude: Double,
        longitude: Double
    ): AlAdhanQiblaResponseDto {
        return httpClient.get("$BASE_URL/v1/qibla/$latitude/$longitude").body()
    }

    private companion object {
        const val BASE_URL = "https://api.aladhan.com"
    }
}