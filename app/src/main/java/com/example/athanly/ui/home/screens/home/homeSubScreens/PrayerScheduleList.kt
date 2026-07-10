package com.example.athanly.ui.home.screens.home.homeSubScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.athanly.R
import com.example.athanly.athanTime.local.domain.modelData.PrayerSchedule
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import com.example.athanly.athanTime.local.domain.modelData.timeFor
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val rowTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

// Fixed 6-row list (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha) — a plain Column, not
// LazyColumn, since the row count is always exactly 6, not a scrollable/dynamic dataset
@Composable
fun PrayerScheduleList(
    schedule: PrayerSchedule,
    nextPrayer: Prayer?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.home_prayer_row_spacing)
        )
    ) {
        Prayer.entries.forEach { prayer ->
            PrayerRow(
                prayer = prayer,
                time = schedule.timeFor(prayer),
                isActive = prayer == nextPrayer
            )
        }
    }
}

@Composable
private fun PrayerRow(
    prayer: Prayer,
    time: LocalTime,
    isActive: Boolean,
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
                .fillMaxWidth()
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
                if (!prayer.isPrayer) {
                    // Sunrise isn't a prayer — a small sun icon distinguishes its row from the 5 real prayers
                    Icon(
                        imageVector = Icons.Filled.WbSunny,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(dimensionResource(R.dimen.home_prayer_row_icon_size))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.home_prayer_row_icon_name_gap)))
                }
                Text(
                    text = stringResource(prayer.displayNameRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
            }
            Text(
                text = time.format(rowTimeFormatter),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = contentColor
            )
        }
    }
}