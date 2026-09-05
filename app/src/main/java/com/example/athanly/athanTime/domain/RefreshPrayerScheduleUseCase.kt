package com.example.athanly.athanTime.domain

import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.local.domain.repository.AthanTimeRepository
import com.example.athanly.athanTime.remote.domain.repository.AthanApiRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

// Shared by HomeViewModel (fetch-on-observe) and PrayerScheduleSyncWorker (daily background
// fetch) so "is today's schedule cached, and if not go get it" isn't implemented twice.
// Throws on network failure — each caller decides how to surface that (HomeViewModel sets
// its own error UI state, the worker returns Result.retry()).
class RefreshPrayerScheduleUseCase @Inject constructor(
    private val athanTimeRepository: AthanTimeRepository,
    private val athanApiRepository: AthanApiRepository
) {
    suspend operator fun invoke(
        city: City,
        today: LocalDate,
        isHanafiMode: Boolean,
        onFetchStarted: () -> Unit = {}
    ) {
        // first() reads just the current cached value rather than subscribing — this only
        // needs a one-time check, not an ongoing observation
        val cachedSchedule = athanTimeRepository.observeSchedule().first()

        // Either signal alone means the cached schedule no longer reflects reality: the day
        // rolled over, or the user flipped Hanafi mode after this schedule was calculated
        val isFresh = cachedSchedule != null &&
            cachedSchedule.date == today &&
            cachedSchedule.isHanafiMode == isHanafiMode

        if (isFresh) return

        // Only fires once staleness is confirmed, right before the real network call — lets
        // callers (HomeViewModel) show a loading state solely during an actual fetch, never
        // during this cheap local check
        onFetchStarted()

        val fresh = athanApiRepository.fetchNewPrayerSchedule(
            latitude = city.latitude,
            longitude = city.longitude,
            date = today,
            useHanafiAsr = isHanafiMode
        )
        athanTimeRepository.saveCityWithSchedule(city, fresh)
    }
}