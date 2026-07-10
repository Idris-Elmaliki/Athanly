package com.example.athanly.athanTime.local.domain.modelData

import java.time.LocalTime

// Hardcoded stand-in for the real prayer-time calculation + city persistence layer (a
// separate future plan). Lets the Home UI be built and reviewed against realistic-looking
// data now — HomeViewModel seeds its initial HomeUiState from these two lists.
val mockCities = listOf(
    City(
        id = "istanbul",
        name = "Istanbul",
        hijriDate = "12 Rabi' al-Awwal, 1445 AH",
    ),
    City(
        id = "makkah",
        name = "Makkah",
        hijriDate = "12 Rabi' al-Awwal, 1445 AH",
    ),
    City(
        id = "new-york",
        name = "New York",
        hijriDate = "12 Rabi' al-Awwal, 1445 AH",
    ),
)

val mockSchedulesByCityId = mapOf(
    "istanbul" to PrayerSchedule(
        fajr = LocalTime.of(4, 30),
        sunrise = LocalTime.of(6, 5),
        dhuhr = LocalTime.of(12, 15),
        asr = LocalTime.of(15, 45),
        maghrib = LocalTime.of(18, 20),
        isha = LocalTime.of(19, 45),
    ),
    "makkah" to PrayerSchedule(
        fajr = LocalTime.of(5, 10),
        sunrise = LocalTime.of(6, 32),
        dhuhr = LocalTime.of(12, 28),
        asr = LocalTime.of(15, 52),
        maghrib = LocalTime.of(18, 6),
        isha = LocalTime.of(19, 36),
    ),
    "new-york" to PrayerSchedule(
        fajr = LocalTime.of(5, 48),
        sunrise = LocalTime.of(7, 12),
        dhuhr = LocalTime.of(12, 4),
        asr = LocalTime.of(15, 20),
        maghrib = LocalTime.of(17, 8),
        isha = LocalTime.of(18, 32),
    ),
)