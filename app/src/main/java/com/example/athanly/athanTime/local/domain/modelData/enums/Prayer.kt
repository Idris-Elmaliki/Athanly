package com.example.athanly.athanTime.local.domain.modelData.enums

import com.example.athanly.R

// Identifies each row in the prayer schedule, in display order. isPrayer = false for
// Sunrise — it's shown for reference but never counted as the "next prayer".
enum class Prayer(val isPrayer: Boolean, val displayNameRes: Int) {
    FAJR(isPrayer = true, displayNameRes = R.string.prayer_fajr),
    SUNRISE(isPrayer = false, displayNameRes = R.string.prayer_sunrise),
    DHUHR(isPrayer = true, displayNameRes = R.string.prayer_dhuhr),
    ASR(isPrayer = true, displayNameRes = R.string.prayer_asr),
    MAGHRIB(isPrayer = true, displayNameRes = R.string.prayer_maghrib),
    ISHA(isPrayer = true, displayNameRes = R.string.prayer_isha)
}