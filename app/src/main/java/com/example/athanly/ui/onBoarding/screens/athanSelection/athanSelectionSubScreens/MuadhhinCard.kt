package com.example.athanly.ui.onBoarding.screens.athanSelection.athanSelectionSubScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.Muadhdhin

@Composable
fun MuadhhinCard(
    muadhdhin: Muadhdhin,
    isSelected: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLow
    val elevation = if (isSelected) dimensionResource(R.dimen.athan_selection_card_active_elevation) else dimensionResource(R.dimen.athan_selection_card_inactive_elevation)

    Card(
        onClick = onClick,
        modifier = Modifier
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.athan_selection_avatar_content_gap)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MuadhhinAvatar(muadhdhin = muadhdhin, isPlaying = isPlaying)
                MuadhhinInfo(muadhdhin = muadhdhin)
            }

            MuadhhinRadio(isSelected = isSelected)
        }
    }
}

@Composable
private fun MuadhhinAvatar(
    muadhdhin: Muadhdhin,
    isPlaying: Boolean
) {
    Box(
        modifier = Modifier
            .size(dimensionResource(R.dimen.athan_selection_avatar_size))
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(muadhdhin.imageRes),
            // Decorative — the location/sheikh text next to it already labels this avatar
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )

        // Subtle dark scrim so the play icon reads clearly against any photo
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = MaterialTheme.colorScheme.scrim,
                    shape = CircleShape,
                ),
        )

        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(dimensionResource(R.dimen.athan_selection_play_icon_size)),
        )
    }
}

@Composable
private fun MuadhhinInfo(muadhdhin: Muadhdhin) {
    Column {
        Text(
            text = stringResource(muadhdhin.locationNameRes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.athan_selection_text_gap)))

        Text(
            text = stringResource(muadhdhin.sheikhNameRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun MuadhhinRadio(isSelected: Boolean) {
    if (isSelected) {
        // Active: fully filled yellow circle with a white checkmark
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
                modifier = Modifier.size(dimensionResource(R.dimen.athan_selection_check_icon_size)),
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