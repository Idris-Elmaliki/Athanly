package com.example.athanly.ui.home.screens.home.compact

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
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

// Compact (< 600dp): single-column, full-bleed. HomeTopBar is fixed above the pager (per
// design it doesn't swipe with city content); everything below it swipes per-city.
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeUiCompact(
    state: HomeUiState,
    onMenuClick: () -> Unit,
    onAddCityClick: () -> Unit,
    onCitySelected: (Int) -> Unit,
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
                onAddCityClick = onAddCityClick,
                modifier = Modifier.padding(top = topBarPadding)
            )

            val pagerState = rememberPagerState(
                initialPage = state.selectedCityIndex
            ) { state.cities.size }

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
                        .padding(horizontal = dimensionResource(R.dimen.home_screen_horizontal_padding))
                        .verticalScroll(rememberScrollState())
                ) {
                    CityDateHeader(cityName = city.name, hijriDate = city.hijriDate)

                    // nextPrayerInfo is only computed for the currently selected city — fine here
                    // since this page only renders it once pagerState/selectedCityIndex agree
                    if (schedule != null && state.nextPrayerInfo != null) {
                        SacredCountdownCard(
                            nextPrayerInfo = state.nextPrayerInfo
                        )

                        Spacer(modifier = Modifier
                            .height(dimensionResource(R.dimen.home_screen_section_spacing))
                        )

                        PrayerScheduleList(
                            schedule = schedule,
                            nextPrayer = state.nextPrayerInfo.nextPrayer
                        )
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