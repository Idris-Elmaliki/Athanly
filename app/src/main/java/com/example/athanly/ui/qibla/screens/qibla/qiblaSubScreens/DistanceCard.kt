package com.example.athanly.ui.qibla.screens.qibla.qiblaSubScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.athanly.R
import com.example.athanly.qibla.domain.local.modelData.enums.CardinalDirection
import com.example.athanly.qibla.domain.local.modelData.enums.DistanceUnit
import java.util.Locale

// Outer card mirrors SacredCountdownCard's info-card convention (Box + clip + plain
// surfaceContainerHigh background, no elevation/border) rather than the
// selectable-card style used by MuadhhinCard, since this card isn't itself selectable
// — only the km/mi toggle inside it is.
@Composable
fun DistanceCard(
    distanceValue: Double,
    distanceUnit: DistanceUnit,
    qiblaDegrees: Double,
    qiblaDirection: CardinalDirection,
    onUnitToggled: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.qibla_distance_card_corner_radius)))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(dimensionResource(R.dimen.qibla_distance_card_padding)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.qibla_distance_card_label),
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = dimensionResource(R.dimen.qibla_distance_card_label_letter_spacing).value.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.qibla_distance_card_label_value_gap)))

            Row(verticalAlignment = Alignment.Bottom) {
                // %,.0f groups thousands (e.g. "10,875") and drops decimals, matching
                // the design's whole-number distance display.
                Text(
                    text = String.format(Locale.getDefault(), "%,.0f", distanceValue),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.qibla_distance_card_unit_value_gap)))

                Text(
                    text = stringResource(distanceUnit.unitLabelRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.qibla_distance_card_value_bearing_gap)))

            Text(
                text = stringResource(
                    R.string.qibla_distance_card_bearing,
                    qiblaDegrees.toInt(),
                    stringResource(qiblaDirection.abbreviationRes)
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiaryContainer
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.qibla_distance_card_bearing_toggle_gap)))

            Row {
                DistanceUnitToggleOption(
                    label = stringResource(R.string.qibla_unit_km),
                    isSelected = distanceUnit == DistanceUnit.KILOMETERS,
                    onSelected = onUnitToggled
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.qibla_distance_card_unit_value_gap)))

                DistanceUnitToggleOption(
                    label = stringResource(R.string.qibla_unit_mi),
                    isSelected = distanceUnit == DistanceUnit.MILES,
                    onSelected = onUnitToggled
                )
            }
        }
    }
}

@Composable
private fun DistanceUnitToggleOption(
    label: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceContainerHigh
    val borderColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val textColor = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(dimensionResource(R.dimen.qibla_unit_toggle_corner_radius)))
            .background(backgroundColor)
            .border(
                width = dimensionResource(R.dimen.qibla_unit_toggle_border_width),
                color = borderColor,
                shape = RoundedCornerShape(dimensionResource(R.dimen.qibla_unit_toggle_corner_radius))
            )
            // Only the non-selected pill reacts — tapping the already-selected pill
            // is a no-op, since onUnitToggled always flips the unit rather than
            // setting a specific target (there are only two options, so the other
            // pill is always the one that needs to react).
            .clickable(enabled = !isSelected, onClick = onSelected)
            .padding(
                horizontal = dimensionResource(R.dimen.qibla_unit_toggle_horizontal_padding),
                vertical = dimensionResource(R.dimen.qibla_unit_toggle_vertical_padding)
            )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}