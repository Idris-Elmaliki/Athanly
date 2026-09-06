package com.example.athanly.ui.onBoarding.screens.location.compact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.athanly.R
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.location.domain.modelData.LocationUiState
import com.example.athanly.location.domain.modelData.presetCities
import com.example.athanly.ui.onBoarding.screens.location.locationSubScreens.LocationPermissionCard
import com.example.athanly.ui.onBoarding.screens.location.locationSubScreens.PresetCityPicker

// Compact layout (0–599dp) — 4 columns, 8dp min margins
@Composable
fun LocationUiCompact(
    state: LocationUiState,
    onShareLocationClick: () -> Unit,
    onPresetCitySelected: (City) -> Unit,
    undoPresetCitySelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                LocationHeader()
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.athan_selection_header_bottom_padding)))
            }

            item {
                LocationPermissionCard(
                    isLoading = state.isLoading,
                    hasSavedCity = state.hasSavedCity,
                    onShareLocationClick = onShareLocationClick,
                )
            }

            // Forced fallback — stays visible until a city exists one way or the other, since
            // OnBoardingLayout's shared Continue button won't advance past this step without it
            if (!state.hasSavedCity) {
                item {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_card_spacing)))

                    Text(
                        text = stringResource(R.string.location_choose_city_heading),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_text_gap)))
                }

                itemsIndexed(presetCities) { index, city ->
                    PresetCityPicker(
                        city = city,
                        isSelected = false,
                        onClick = { onPresetCitySelected(city) },
                    )

                    if (index < presetCities.lastIndex) {
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_card_spacing)))
                    }
                }
            }
            else if(state.hasSavedCity && !state.isPermissionGranted){
                item {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_text_gap)))

                    PresetCityPicker(
                        city = state.city ?: presetCities[0],
                        isSelected = true,
                        onClick = { undoPresetCitySelected() },
                        modifier = Modifier
                    )
                }
            }

            item {
                // Clears the fixed bottom bar + its gradient fade since Scaffold padding is suppressed
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.athan_selection_scroll_bottom_padding)))
            }
        }
    }
}

@Composable
private fun LocationHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding)),
    ) {
        Text(
            text = stringResource(R.string.location_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_text_gap)))

        Text(
            text = stringResource(R.string.location_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}