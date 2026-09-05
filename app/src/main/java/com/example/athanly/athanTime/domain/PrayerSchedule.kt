package com.example.athanly.athanTime.domain

import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import java.time.LocalDate
import java.time.LocalTime

// The 5 daily prayer times + Sunrise for one City, for one day. LocalTime (not a formatted
// String) so the ViewModel can do real countdown/next-prayer math against the current time.
// `date` records which calendar day these times are for — the future "only call the Athan API
// once per city per day" check compares this against today's date to decide staleness.
data class PrayerSchedule(
    val date: LocalDate = LocalDate.now(),

    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime,

    // Which Asr calculation mode this Asr time was actually computed with — lets a later
    // staleness check catch "the user flipped Hanafi mode but Asr is still the old value"
    // independently of whether the date itself is still today
    val isHanafiMode: Boolean = false
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