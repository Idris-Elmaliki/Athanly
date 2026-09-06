package com.example.athanly.ui.home.screens.home.homeSubScreens.changeCity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.athanly.R
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.location.domain.modelData.LocationUiState
import com.example.athanly.location.domain.modelData.presetCities
import com.example.athanly.ui.onBoarding.screens.location.locationSubScreens.PresetCityPicker
import com.example.athanly.ui.subscreens.DetailTopBar

// Compact (< 600dp): single-column, full-bleed — same shell shape as MuadhdhinPickerUiCompact.
// Unlike onboarding's LocationUiCompact, nothing here ever hides — the full list (GPS row +
// every preset) always shows, with a checkmark on whichever one matches the current city, same
// as MuadhdhinPickerUiCompact's checkmark-on-selected-row treatment.
@Composable
fun ChangeCityUiCompact(
    modifier: Modifier = Modifier,
    state: LocationUiState,
    onShareLocationClick: () -> Unit,
    onPresetCitySelected: (City) -> Unit,
    onBackClick: () -> Unit,
    topBarPadding: Dp
) {
    // A GPS-fetched city won't equal any preset (different lat/lng), so "not one of the
    // presets" is enough to mean "current location" without a dedicated source flag on City
    val isCurrentLocationSelected = state.city != null && state.city !in presetCities

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(
                onBackClick = onBackClick,
                modifier = Modifier.padding(top = topBarPadding)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.settings_screen_horizontal_padding))
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.welcome_card_spacing))
            ) {
                item {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.settings_screen_top_content_padding)))
                }

                item {
                    CurrentLocationRow(
                        isSelected = isCurrentLocationSelected,
                        isLoading = state.isLoading,
                        onClick = onShareLocationClick
                    )
                }

                items(presetCities) { city ->
                    PresetCityPicker(
                        city = city,
                        isSelected = city == state.city,
                        onClick = { onPresetCitySelected(city) }
                    )
                }
            }
        }
    }
}

// Same Card/border/checkmark visuals as PresetCityPicker, so the GPS option reads as one more
// row in the same list rather than a visually distinct control
@Composable
private fun CurrentLocationRow(
    isSelected: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLow

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = dimensionResource(R.dimen.welcome_radio_button_border_width),
                color = borderColor,
                shape = RoundedCornerShape(dimensionResource(R.dimen.welcome_card_corner_radius))
            ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.welcome_card_corner_radius)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.welcome_card_horizontal_padding),
                    vertical = dimensionResource(R.dimen.welcome_card_vertical_padding)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensionResource(R.dimen.location_card_button_icon_size))
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.location_card_button_icon_gap)))

                Text(
                    text = stringResource(R.string.change_city_current_location_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimensionResource(R.dimen.location_card_button_spinner_size)),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = dimensionResource(R.dimen.location_card_button_spinner_stroke_width)
                    )
                }
                isSelected -> {
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.welcome_radio_button_size))
                            .background(color = MaterialTheme.colorScheme.tertiary, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(dimensionResource(R.dimen.welcome_radio_button_inner_size))
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.welcome_radio_button_size))
                            .border(
                                width = dimensionResource(R.dimen.welcome_radio_button_border_width),
                                color = MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}