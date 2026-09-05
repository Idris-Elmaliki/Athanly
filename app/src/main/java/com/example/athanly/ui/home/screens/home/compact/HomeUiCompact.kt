package com.example.athanly.ui.home.screens.home.compact

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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

// Compact (< 600dp): single-column, full-bleed. One location only — no more per-city pager.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUiCompact(
    state: HomeUiState,
    onMenuClick: () -> Unit,
    topBarPadding: Dp,
    bottomBarPadding: Dp,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        // Unpadded — stays full-bleed behind the floating BottomNavBar instead of stopping
        // above it, so the bar's rounded corners reveal real content instead of dead space
        AtmosphericBackground()

        // Real, interactive content gets bottomBarPadding so it stops above the nav bar
        // instead of rendering underneath it
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomBarPadding)
        ) {
            HomeTopBar(
                onMenuClick = onMenuClick,
                modifier = Modifier.padding(top = topBarPadding)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.home_screen_horizontal_padding))
                    .verticalScroll(rememberScrollState())
            ) {
                val city = state.city
                val schedule = state.schedule

                // city is null until onboarding's Location step (or the manual fallback
                // picker) saves one — nothing to head the screen with until then
                if (city != null) {
                    CityDateHeader(
                        cityName = city.name,
                        hijriDate = city.hijriDate
                    )
                }

                // Mounted once there's anything to show — real data or a loading state — and
                // never unmounted between those two, so SacredCountdownCard/PrayerScheduleList
                // keep their own shells stable and only swap their internal content (see those
                // composables). isLoading covers an active network fetch; isInitializing covers
                // the brief window before Room's first emission.

                SacredCountdownCard(
                    nextPrayerInfo = state.nextPrayerInfo,
                    is24HourFormat = state.is24HourFormat
                )

                Spacer(modifier = Modifier
                    .height(dimensionResource(R.dimen.home_screen_section_spacing))
                )

                PrayerScheduleList(
                    schedule = schedule,
                    nextPrayer = state.nextPrayerInfo?.nextPrayer,
                    is24HourFormat = state.is24HourFormat
                )
            }
        }
    }
}