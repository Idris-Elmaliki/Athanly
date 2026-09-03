package com.example.athanly.ui.onBoarding.screens.location.locationSubScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.athanly.R
import com.example.athanly.athanTime.local.domain.modelData.City

// One selectable row for the forced city fallback list — same card/radio shape as
// MuadhhinCard on the AthanSelection step, minus the avatar/subtitle since a city only
// needs a name. The screen's own LazyColumn loops this per-city (itemsIndexed(presetCities)),
// same as AthanSelectionUiCompact does for MuadhhinCard — a LazyColumn can't itself nest inside
// another LazyColumn/scrollable Column without a bounded height, so this stays a single row.
@Composable
fun PresetCityPicker(
    city: City,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
                shape = RoundedCornerShape(dimensionResource(R.dimen.welcome_card_corner_radius)),
            ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.welcome_card_corner_radius)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.welcome_card_horizontal_padding),
                    vertical = dimensionResource(R.dimen.welcome_card_vertical_padding),
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = city.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            if (isSelected) {
                // Active: fully filled circle with a checkmark — same as MuadhhinRadio
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.welcome_radio_button_size))
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(dimensionResource(R.dimen.welcome_radio_button_inner_size)),
                    )
                }
            } else {
                // Inactive: grey ring only — no fill, no inner dot
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.welcome_radio_button_size))
                        .border(
                            width = dimensionResource(R.dimen.welcome_radio_button_border_width),
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}