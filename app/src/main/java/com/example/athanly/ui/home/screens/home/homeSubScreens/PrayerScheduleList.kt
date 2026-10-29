package com.example.athanly.ui.home.screens.home.homeSubScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.athanly.R
import com.example.athanly.athanTime.domain.PrayerSchedule
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import com.example.athanly.athanTime.domain.timeFor
import com.example.athanly.ui.subscreens.SkeletonBar
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 6-row list (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha) — always renders all 6 row shells,
// schedule null or not, so loading never tears rows down/rebuilds them (see PrayerRow)
@Composable
fun PrayerScheduleList(
    schedule: PrayerSchedule?,
    nextPrayer: Prayer?,
    is24HourFormat: Boolean,
    modifier: Modifier = Modifier
) {
    // Settings' time format toggle drives this — "HH:mm" (24hr) vs "h:mm a" (12hr, e.g. "4:30 PM").
    // remember(is24HourFormat, appLocale) avoids rebuilding the formatter on every recomposition,
    // only when the toggle or the app language actually changes. The app's locale (not the
    // device's) keeps AM/PM in the chosen language; digits stay Latin whatever the locale.
    val appLocale = LocalConfiguration.current.locales[0]
    val rowTimeFormatter = remember(is24HourFormat, appLocale) {
        DateTimeFormatter.ofPattern(if (is24HourFormat) "HH:mm" else "h:mm a", appLocale)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.home_prayer_row_spacing)
        )
    ) {
        Prayer.entries.forEach { prayer ->
            PrayerRow(
                prayer = prayer,
                time = schedule?.timeFor(prayer),
                isActive = prayer == nextPrayer,
                timeFormatter = rowTimeFormatter
            )
        }
    }
}

// The icon and prayer name are static per-Prayer data (not fetched), so they always render
// for real — only the time on the right is genuinely unknown until schedule loads, so that's
// the only piece that swaps to a shimmer placeholder. The row's own shell (shape/background/
// border/accent) is unconditional either way, same reasoning as SacredCountdownCard.
@Composable
private fun PrayerRow(
    prayer: Prayer,
    time: LocalTime?,
    isActive: Boolean,
    timeFormatter: DateTimeFormatter,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    val contentColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val shape = RoundedCornerShape(dimensionResource(R.dimen.home_prayer_row_corner_radius))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.home_prayer_row_height))
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (isActive) {
                    Modifier.border(
                        width = dimensionResource(R.dimen.home_prayer_row_active_border_width),
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
    ) {
        if (isActive) {
            // Accent bar drawn under the row content, aligned to the start edge
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(dimensionResource(R.dimen.home_prayer_row_active_accent_width))
                    .background(MaterialTheme.colorScheme.tertiary, shape)
                    .align(Alignment.CenterStart)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = if (isActive) {
                        dimensionResource(R.dimen.home_prayer_row_active_start_padding)
                    } else {
                        dimensionResource(R.dimen.home_prayer_row_horizontal_padding)
                    },
                    end = dimensionResource(R.dimen.home_prayer_row_horizontal_padding)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = prayer.icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(dimensionResource(R.dimen.home_prayer_row_icon_size))
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.home_prayer_row_icon_name_gap)))
                Text(
                    text = stringResource(prayer.displayNameRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
            }
            if (time != null) {
                Text(
                    text = time.format(timeFormatter),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = contentColor
                )
            } else {
                SkeletonBar(
                    width = dimensionResource(R.dimen.home_skeleton_prayer_time_bar_width),
                    height = dimensionResource(R.dimen.home_skeleton_prayer_time_bar_height)
                )
            }
        }
    }
}