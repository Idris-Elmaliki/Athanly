package com.example.athanly.athanTime.local.domain.modelData

import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import java.time.LocalTime

// The 5 daily prayer times + Sunrise for one City, for one day. LocalTime (not a formatted
// String) so the ViewModel can do real countdown/next-prayer math against the current time.
data class PrayerSchedule(
    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime
)

// Looks up this schedule's time for a given Prayer — keeps callers (e.g. next-prayer math)
// from needing their own when-over-Prayer mapping
fun PrayerSchedule.timeFor(prayer: Prayer): LocalTime {
    return when (prayer) {
        Prayer.FAJR -> fajr
        Prayer.SUNRISE -> sunrise
        Prayer.DHUHR -> dhuhr
        Prayer.ASR -> asr
        Prayer.MAGHRIB -> maghrib
        Prayer.ISHA -> isha
    }
}