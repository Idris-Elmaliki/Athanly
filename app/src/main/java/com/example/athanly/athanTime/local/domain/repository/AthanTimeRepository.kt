package com.example.athanly.athanTime.local.domain.repository

import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.domain.PrayerSchedule
import kotlinx.coroutines.flow.Flow

// Only domain types cross this boundary — Room entities never leak past the data layer.
// There's exactly one city now (see City.kt) — no id is needed to address it.
interface AthanTimeRepository {

    fun observeCity(): Flow<City?>

    fun observeSchedule(): Flow<PrayerSchedule?>

    // Onboarding's Location step calls this alone, before any schedule exists yet —
    // HomeViewModel's refresh flow fetches and saves the schedule once it observes the city.
    suspend fun saveCity(city: City)

    suspend fun saveCityWithSchedule(
        city: City,
        schedule: PrayerSchedule
    )
}