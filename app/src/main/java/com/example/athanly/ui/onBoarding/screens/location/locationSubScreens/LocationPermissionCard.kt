package com.example.athanly.ui.onBoarding.screens.location.locationSubScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.athanly.R

// Standard glassmorphism card, same shape/style as HanafiCard on the Calculations step —
// keeps the Location step visually consistent with the rest of the onboarding pagination
// system instead of introducing a one-off illustration layout.
@Composable
fun LocationPermissionCard(
    isLoading: Boolean,
    hasSavedCity: Boolean,
    onShareLocationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.prayer_settings_card_corner_radius)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        ),
        border = BorderStroke(
            width = CardDefaults.outlinedCardBorder().width,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        ),
        elevation = CardDefaults.outlinedCardElevation(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.prayer_settings_card_horizontal_padding),
                    vertical = dimensionResource(R.dimen.prayer_settings_card_vertical_padding),
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.location_card_icon_circle_size))
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(dimensionResource(R.dimen.location_card_icon_size)),
                    )
                }

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.prayer_settings_card_description_end_padding)))

                Column {
                    Text(
                        text = stringResource(R.string.location_card_title),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier.height(dimensionResource(R.dimen.prayer_settings_card_text_gap)))

                    Text(
                        text = stringResource(R.string.location_card_description),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.prayer_settings_header_bottom_spacing)))

            // hasSavedCity is sourced from Room (see OnBoardingViewModel), so this also
            // flips to the confirmation row if the user ends up picking a preset city instead
            if (hasSavedCity) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensionResource(R.dimen.location_card_success_icon_size)),
                    )

                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.location_card_success_gap)))

                    Text(
                        text = stringResource(R.string.location_card_saved),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                Button(
                    onClick = onShareLocationClick,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.location_card_button_corner_radius)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    contentPadding = PaddingValues(
                        vertical = dimensionResource(R.dimen.location_card_button_vertical_padding),
                    ),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(dimensionResource(R.dimen.location_card_button_spinner_size)),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = dimensionResource(R.dimen.location_card_button_spinner_stroke_width),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(dimensionResource(R.dimen.location_card_button_icon_size)),
                        )

                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.location_card_button_icon_gap)))

                        Text(
                            text = stringResource(R.string.location_card_button),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}