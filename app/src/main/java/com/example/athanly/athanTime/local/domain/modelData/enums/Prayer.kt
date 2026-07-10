package com.example.athanly.athanTime.local.domain.modelData.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.athanly.R

// Identifies each row in the prayer schedule, in display order. isPrayer = false for
// Sunrise — it's shown for reference but never counted as the "next prayer".
enum class Prayer(
    val isPrayer: Boolean,
    val displayNameRes: Int,
    val icon: ImageVector
) {
    FAJR(
        isPrayer = true,
        displayNameRes = R.string.prayer_fajr,
        icon = Icons.Filled.WbTwilight
    ),
    SUNRISE(
        isPrayer = false,
        displayNameRes = R.string.prayer_sunrise,
        icon = Icons.Filled.WbSunny
    ),
    DHUHR(
        isPrayer = true,
        displayNameRes = R.string.prayer_dhuhr,
        icon = Icons.Filled.LightMode
    ),
    ASR(
        isPrayer = true,
        displayNameRes = R.string.prayer_asr,
        icon = Icons.Filled.Brightness6
    ),
    MAGHRIB(
        isPrayer = true,
        displayNameRes = R.string.prayer_maghrib,
        icon = Icons.Filled.NightsStay
    ),
    ISHA(
        isPrayer = true,
        displayNameRes = R.string.prayer_isha,
        icon = Icons.Filled.Bedtime
    )
}