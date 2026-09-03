package com.example.athanly.athanTime.local.domain.modelData

import com.example.athanly.athanTime.domain.PrayerSchedule

// Lives alongside City/PrayerSchedule in athanTime — the Home screen's state is built
// entirely from athan data, so it belongs with the rest of the athan domain models.
data class HomeUiState(
    // Only true while HomeViewModel is actively fetching a fresh schedule from the network
    val isLoading: Boolean = false,
    // True until Room's observeCity()/observeSchedule() have emitted at least once — covers
    // the brief DB-read window isLoading doesn't, without racing it (two independent
    // collectors write these, so they stay separate fields rather than sharing one)
    val isInitializing: Boolean = true,
    val error: String? = null,

    // Both null until the Location onboarding step saves a city and HomeViewModel's
    // refresh flow fetches its schedule
    val city: City? = null,
    val schedule: PrayerSchedule? = null,

    // Null until the first tick computes it — recalculated every second by HomeViewModel
    val nextPrayerInfo: NextPrayerInfo? = null
)