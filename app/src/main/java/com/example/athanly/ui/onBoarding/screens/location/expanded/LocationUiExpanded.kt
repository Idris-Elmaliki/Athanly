package com.example.athanly.ui.onBoarding.screens.location.expanded

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

// Expanded layout (840dp+) — 12 columns, 32dp min margins, 2-column card grid
@Composable
fun LocationUiExpanded(
    state: LocationUiState,
    onShareLocationClick: () -> Unit,
    onPresetCitySelected: (City) -> Unit,
    undoPresetCitySelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding),
                vertical = dimensionResource(R.dimen.welcome_screen_horizontal_padding),
            ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.welcome_card_spacing)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.welcome_card_spacing)),
        ) {
            // Header spans both columns
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.athan_selection_header_bottom_padding))
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LocationExpandedHeader()
                }
            }

            // Wide info card also spans both columns, same as the header
            item(span = { GridItemSpan(maxLineSpan) }) {
                LocationPermissionCard(
                    isLoading = state.isLoading,
                    hasSavedCity = state.hasSavedCity,
                    onShareLocationClick = onShareLocationClick,
                )
            }

            // Forced fallback — stays visible until a city exists one way or the other, since
            // OnBoardingLayout's shared Continue button won't advance past this step without it
            if (!state.hasSavedCity) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(R.string.location_choose_city_heading),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                items(presetCities) { city ->
                    PresetCityPicker(
                        city = city,
                        isSelected = false,
                        onClick = { onPresetCitySelected(city) },
                    )
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

            // Bottom spacer spans both columns to clear the fixed bottom bar
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.athan_selection_scroll_bottom_padding)))
            }
        }
    }
}

@Composable
private fun LocationExpandedHeader() {
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