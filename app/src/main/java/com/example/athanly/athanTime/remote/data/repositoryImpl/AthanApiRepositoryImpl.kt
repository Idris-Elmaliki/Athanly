package com.example.athanly.athanTime.remote.data.repositoryImpl

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
    override suspend fun fetchPrayerSchedule(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        useHanafiAsr: Boolean
    ): PrayerSchedule {
        val school = if (useHanafiAsr) 
                        HANAFI_SCHOOL 
                    else 
                        SHAFI_SCHOOL
        
        val response = apiService.getTimings(
            date, 
            latitude, 
            longitude, 
            school
        )
        
        return response.data.timings.toDomain(date)
    }

    private companion object {
        const val SHAFI_SCHOOL = 0
        const val HANAFI_SCHOOL = 1
    }
}

private fun AlAdhanTimingsDto.toDomain(date: LocalDate) = PrayerSchedule(
    date = date,
    fajr = fajr.toLocalTime(),
    sunrise = sunrise.toLocalTime(),
    dhuhr = dhuhr.toLocalTime(),
    asr = asr.toLocalTime(),
    maghrib = maghrib.toLocalTime(),
    isha = isha.toLocalTime()
)