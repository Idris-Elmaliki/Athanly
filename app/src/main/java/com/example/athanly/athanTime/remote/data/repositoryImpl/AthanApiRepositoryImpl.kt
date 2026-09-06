package com.example.athanly.athanTime.remote.data.repositoryImpl

import android.util.Log
import com.example.athanly.athanTime.domain.PrayerSchedule
import com.example.athanly.athanTime.remote.domain.api.AlAdhanApiService
import com.example.athanly.athanTime.remote.data.dto.AlAdhanTimingsDto
import com.example.athanly.athanTime.remote.domain.repository.AthanApiRepository
import com.example.athanly.userInfoCalculations.toLocalTime
import java.time.LocalDate
import javax.inject.Inject

class AthanApiRepositoryImpl @Inject constructor(
    private val apiService: AlAdhanApiService
) : AthanApiRepository {
    override suspend fun fetchNewPrayerSchedule(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        useHanafiAsr: Boolean
    ): PrayerSchedule {
        val school =
            if(useHanafiAsr) {
                HANAFI_SCHOOL
            }
            else {
                SHAFI_SCHOOL
            }

        val response = apiService.getTimings(
            date = date,
            latitude = latitude,
            longitude = longitude,
            school = school
        )

        return response.data.timings.toDomain(date, useHanafiAsr, latitude, longitude)
    }

    private companion object {
        const val SHAFI_SCHOOL = 0
        const val HANAFI_SCHOOL = 1
    }
}

private fun AlAdhanTimingsDto.toDomain(
    date: LocalDate,
    isHanafiMode: Boolean,
    latitude: Double,
    longitude: Double
) = PrayerSchedule(
    date = date,
    fajr = fajr.toLocalTime(),
    sunrise = sunrise.toLocalTime(),
    dhuhr = dhuhr.toLocalTime(),
    asr = asr.toLocalTime(),
    maghrib = maghrib.toLocalTime(),
    isha = isha.toLocalTime(),
    isHanafiMode = isHanafiMode,
    latitude = latitude,
    longitude = longitude
)