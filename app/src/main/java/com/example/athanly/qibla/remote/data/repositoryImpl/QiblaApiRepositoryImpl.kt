package com.example.athanly.qibla.remote.data.repositoryImpl

import com.example.athanly.qibla.remote.domain.api.AlAdhanQiblaApiService
import com.example.athanly.qibla.remote.domain.repository.QiblaApiRepository
import javax.inject.Inject

class QiblaApiRepositoryImpl @Inject constructor(
    private val apiService: AlAdhanQiblaApiService
) : QiblaApiRepository {
    override suspend fun fetchQiblaBearing(
        latitude: Double,
        longitude: Double
    ): Double {
        return apiService.getQiblaDirection(
            latitude = latitude,
            longitude = longitude
        ).data.direction
    }
}