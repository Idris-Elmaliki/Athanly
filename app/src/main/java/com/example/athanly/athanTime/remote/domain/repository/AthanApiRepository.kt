package com.example.athanly.athanTime.remote.domain.repository

import com.example.athanly.athanTime.domain.PrayerSchedule
import java.time.LocalDate

// Returns the same PrayerSchedule domain type the local repository uses — the API is just
// another source for it, not a separate concept. useHanafiAsr is passed in by the caller
// since this repository doesn't own user preference state.
interface AthanApiRepository {
    suspend fun fetchNewPrayerSchedule(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        useHanafiAsr: Boolean
    ): PrayerSchedule
}