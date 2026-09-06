package com.example.athanly.ui.home.screens.home.homeSubScreens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.athanly.R

// Single location now — the one button left opens ChangeCity instead of an add-city flow
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.home_top_bar_height))
            .padding(horizontal = dimensionResource(R.dimen.home_top_bar_horizontal_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(dimensionResource(R.dimen.home_top_bar_button_size))
        ) {
            Icon(
                imageVector = Icons.Filled.AddLocationAlt,
                contentDescription = stringResource(R.string.home_menu_button_description),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.home_top_bar_icon_size))
            )
        }
    }
}