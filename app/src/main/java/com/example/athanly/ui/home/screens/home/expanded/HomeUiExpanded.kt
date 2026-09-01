package com.example.athanly.ui.home.screens.home.expanded

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.example.athanly.R
import com.example.athanly.athanTime.local.domain.modelData.HomeUiState
import com.example.athanly.ui.home.screens.home.homeSubScreens.CityDateHeader
import com.example.athanly.ui.home.screens.home.homeSubScreens.HomeTopBar
import com.example.athanly.ui.home.screens.home.homeSubScreens.PrayerScheduleList
import com.example.athanly.ui.home.screens.home.homeSubScreens.SacredCountdownCard
import com.example.athanly.ui.subscreens.AtmosphericBackground

// Expanded (840dp+) — 12 columns, 32dp min margins. Same structure as Compact/Medium, content
// capped at 55% width to keep it readable on large displays, mirroring WelcomeUiExpanded.
// One location only — no more per-city pager.
@Composable
fun HomeUiExpanded(
    state: HomeUiState,
    onMenuClick: () -> Unit,
    topBarPadding: Dp,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        AtmosphericBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(fraction = 0.55f)
                .verticalScroll(rememberScrollState())
        ) {
            HomeTopBar(
                onMenuClick = onMenuClick,
                modifier = Modifier.padding(top = topBarPadding)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.home_screen_horizontal_padding))
            ) {
                val city = state.city
                val schedule = state.schedule

                // city is null until onboarding's Location step (or the manual fallback
                // picker) saves one — nothing to head the screen with until then
                if (city != null) {
                    CityDateHeader(cityName = city.name, hijriDate = city.hijriDate)
                }

                // schedule/nextPrayerInfo are only both non-null once the fetch-and-cache
                // flow in HomeViewModel has actually run for the saved city
                if (schedule != null && state.nextPrayerInfo != null) {
                    SacredCountdownCard(nextPrayerInfo = state.nextPrayerInfo)
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.home_screen_section_spacing)))
                    PrayerScheduleList(schedule = schedule, nextPrayer = state.nextPrayerInfo.nextPrayer)
                }
            }
        }
    }
}