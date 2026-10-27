package com.example.athanly.ui.home.screens.qibla.qiblaSubScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.athanly.R

// Floats over the compass/distance content instead of replacing it outright — the vertical
// gradient (transparent -> background -> transparent) lets the frozen compass bleed through
// at the top/bottom edges rather than presenting a hard-edged card.
@Composable
fun LocationOffBlocker(
    isLocationPermissionGranted: Boolean,
    onOpenAppSettingsClick: () -> Unit,
    onEnableLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Denied permission is fixed in the app's settings page; permission granted but the
    // device toggle off is fixed in location settings — so wording and action follow together.
    val messageRes =
        if (isLocationPermissionGranted) R.string.qibla_location_off_message
        else R.string.qibla_permission_denied_message
    val buttonRes =
        if (isLocationPermissionGranted) R.string.qibla_location_off_button
        else R.string.qibla_permission_denied_button
    val onButtonClick =
        if (isLocationPermissionGranted) onEnableLocationClick
        else onOpenAppSettingsClick

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        // Theme background at 0 alpha — fades to the same hue, not a new color.
                        0f to MaterialTheme.colorScheme.background.copy(alpha = 0f),
                        0.2f to MaterialTheme.colorScheme.background,
                        0.8f to MaterialTheme.colorScheme.background,
                        1f to MaterialTheme.colorScheme.background.copy(alpha = 0f)
                    )
                )
            )
            .padding(vertical = dimensionResource(R.dimen.qibla_location_blocker_vertical_padding)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.qibla_location_blocker_content_horizontal_padding)
            )
        ) {
            Text(
                text = stringResource(messageRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.qibla_location_blocker_message_button_gap)))

            Button(
                onClick = onButtonClick,
                // Full pill shape, matching OnBoardingBottomBar's CTA convention.
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(R.dimen.qibla_location_blocker_button_horizontal_padding),
                    vertical = dimensionResource(R.dimen.qibla_location_blocker_button_vertical_padding)
                )
            ) {
                Text(
                    text = stringResource(buttonRes),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}