package com.example.athanly.ui.home.screens.home.medium

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.example.athanly.R
import com.example.athanly.athanTime.local.domain.modelData.HomeUiState
import com.example.athanly.ui.home.screens.home.homeSubScreens.CityDateHeader
import com.example.athanly.ui.home.screens.home.homeSubScreens.CityPaginationDots
import com.example.athanly.ui.home.screens.home.homeSubScreens.HomeTopBar
import com.example.athanly.ui.home.screens.home.homeSubScreens.PrayerScheduleList
import com.example.athanly.ui.home.screens.home.homeSubScreens.SacredCountdownCard
import com.example.athanly.ui.subscreens.AtmosphericBackground

// Medium (600–839dp) — 12 columns, 12dp min margins. Same structure as Compact, content
// capped at 75% width to avoid over-stretching, mirroring WelcomeUiMedium's convention.
@Composable
fun HomeUiMedium(
    state: HomeUiState,
    onMenuClick: () -> Unit,
    onAddCityClick: () -> Unit,
    onCitySelected: (Int) -> Unit,
    topBarPadding: Dp,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        AtmosphericBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(fraction = 0.75f)
        ) {
            HomeTopBar(
                onMenuClick = onMenuClick,
                onAddCityClick = onAddCityClick,
                modifier = Modifier.padding(top = topBarPadding)
            )

            val pagerState = rememberPagerState(initialPage = state.selectedCityIndex) { state.cities.size }

            // Keeps HomeViewModel's selectedCityIndex in sync as the user swipes
            LaunchedEffect(pagerState.currentPage) {
                onCitySelected(pagerState.currentPage)
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val city = state.cities[page]
                val schedule = state.schedulesByCityId[city.id]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = dimensionResource(R.dimen.home_screen_horizontal_padding))
                ) {
                    CityDateHeader(cityName = city.name, hijriDate = city.hijriDate)

                    // nextPrayerInfo is only computed for the currently selected city — fine here
                    // since this page only renders it once pagerState/selectedCityIndex agree
                    if (schedule != null && state.nextPrayerInfo != null) {
                        SacredCountdownCard(nextPrayerInfo = state.nextPrayerInfo)
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.home_screen_section_spacing)))
                        PrayerScheduleList(schedule = schedule, nextPrayer = state.nextPrayerInfo.nextPrayer)
                    }
                }
            }

            CityPaginationDots(
                currentIndex = pagerState.currentPage,
                totalCities = state.cities.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.home_screen_pagination_vertical_padding)),
            )
        }
    }
}