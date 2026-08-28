package com.example.athanly.ui.subscreens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.athanly.R

// Shared by every screen pushed on top of Settings (Language/Muadhdhin pickers) — just the
// back arrow, no title text. AutoMirrored so it flips to point right under RTL (Arabic).
@Composable
fun DetailTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.settings_top_bar_height))
            .padding(horizontal = dimensionResource(R.dimen.settings_top_bar_horizontal_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(dimensionResource(R.dimen.settings_top_bar_button_size))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                // Decorative — its function (go back) is conveyed by position/behavior, no label needed
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(R.dimen.settings_top_bar_icon_size))
            )
        }
    }
}