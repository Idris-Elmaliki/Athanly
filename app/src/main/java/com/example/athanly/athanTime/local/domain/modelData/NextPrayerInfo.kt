package com.example.athanly.athanTime.local.domain.modelData

import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import java.time.Duration
import java.time.LocalTime

// Derived, not persisted — recomputed every tick from a PrayerSchedule + the current time.
// progressFraction (0f-1f) is how far elapsed we are between currentPrayer and nextPrayer;
// drives the Sacred Card's progress ring.
data class NextPrayerInfo(
    val currentPrayer: Prayer,
    val nextPrayer: Prayer,
    val startsAt: LocalTime,
    val remaining: Duration,
    val progressFraction: Float
)