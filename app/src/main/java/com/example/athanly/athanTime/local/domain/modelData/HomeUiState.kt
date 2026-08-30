package com.example.athanly.athanTime.local.domain.modelData

import com.example.athanly.athanTime.domain.PrayerSchedule

// Lives alongside City/PrayerSchedule in athanTime — the Home screen's state is built
// entirely from athan data, so it belongs with the rest of the athan domain models.
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val cities: List<City> = emptyList(),
    val schedulesByCityId: Map<String, PrayerSchedule> = emptyMap(),
    val selectedCityIndex: Int = 0,

    // Null until the first tick computes it — recalculated every second by HomeViewModel
    val nextPrayerInfo: NextPrayerInfo? = null
)