package com.example.athanly.athanTime.local.domain.repository

import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.domain.PrayerSchedule
import kotlinx.coroutines.flow.Flow

// Only domain types cross this boundary — Room entities never leak past the data layer.
interface AthanTimeRepository {

    fun observeCities(): Flow<List<City>>

    fun observeSchedule(cityId: String): Flow<PrayerSchedule?>

    suspend fun saveCityWithSchedule(
        city: City,
        schedule: PrayerSchedule
    )

    suspend fun deleteCity(cityId: String)
}