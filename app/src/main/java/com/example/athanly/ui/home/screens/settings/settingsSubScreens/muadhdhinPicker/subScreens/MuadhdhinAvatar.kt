package com.example.athanly.ui.home.screens.settings.settingsSubScreens.muadhdhinPicker.subScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.Muadhdhin

// Shared by MuadhdhinPickerUiCompact/Medium/Expanded — same circular photo + scrim + play/pause
// icon treatment in every size class, so it lives once here instead of 3 duplicated copies.
@Composable
fun MuadhdhinAvatar(
    muadhdhin: Muadhdhin,
    isPlaying: Boolean
) {
    Box(
        modifier = Modifier
            .size(dimensionResource(R.dimen.athan_selection_avatar_size))
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(muadhdhin.imageRes),
            // Decorative — the location/sheikh text next to it already labels this avatar
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Subtle dark scrim so the play icon reads clearly against any photo
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = MaterialTheme.colorScheme.scrim,
                    shape = CircleShape
                )
        )

        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(dimensionResource(R.dimen.athan_selection_play_icon_size))
        )
    }
}