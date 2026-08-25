package com.example.athanly.ui.home.screens.settings.settingsSubScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.athanly.R

// Own file rather than reusing HomeTopBar/QiblaTopBar — same reasoning as QiblaTopBar:
// Settings has no right-side button, so it needs its own balancing-spacer layout.
@Composable
fun SettingsTopBar(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.settings_top_bar_height))
            .padding(horizontal = dimensionResource(R.dimen.settings_top_bar_horizontal_padding)),
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(dimensionResource(R.dimen.settings_top_bar_button_size))
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                // Reuses the existing menu-button description — same action as Home/Qibla's hamburger button
                contentDescription = stringResource(R.string.home_menu_button_description),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(R.dimen.settings_top_bar_icon_size))
            )
        }

        Spacer(
            modifier = Modifier
                .weight(1f)
        )

        // Balances the hamburger button on the left so the title stays visually centered
        Box(modifier = Modifier
            .size(dimensionResource(R.dimen.settings_top_bar_button_size))
        )
    }
}
