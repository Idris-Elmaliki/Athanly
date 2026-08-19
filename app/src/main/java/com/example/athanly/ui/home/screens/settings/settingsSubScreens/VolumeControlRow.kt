package com.example.athanly.ui.home.screens.settings.settingsSubScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.athanly.R

// Separate from SettingsRow — a slider needs full width beneath the title instead of a
// trailing slot, and has no subtitle line, so it doesn't fit the shared row shape.
@Composable
fun VolumeControlRow(
    volume: Float,
    onVolumeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.settings_row_horizontal_padding),
                vertical = dimensionResource(R.dimen.settings_row_vertical_padding)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(dimensionResource(R.dimen.settings_row_icon_circle_size))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(dimensionResource(R.dimen.settings_row_icon_size))
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(R.dimen.settings_row_icon_text_gap))
        ) {
            Text(
                text = stringResource(R.string.settings_volume_control_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.settings_volume_row_title_slider_gap)))

            Slider(
                value = volume,
                onValueChange = onVolumeChanged,
                colors = SliderDefaults.colors(
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
