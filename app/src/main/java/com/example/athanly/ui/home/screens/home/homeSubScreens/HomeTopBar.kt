package com.example.athanly.ui.home.screens.home.homeSubScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

// Fixed above the swipeable city pager (per design, the top bar itself doesn't swipe with content)
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit,
    onAddCityClick: () -> Unit,
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
                imageVector = Icons.Filled.Menu,
                contentDescription = stringResource(R.string.home_menu_button_description),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(R.dimen.home_top_bar_icon_size))
            )
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            // .uppercase() transforms the app_name resource for display — the resource
            // itself stays in proper case for use elsewhere (e.g. accessibility, launcher label)
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    letterSpacing = dimensionResource(R.dimen.home_top_bar_title_letter_spacing).value.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = onAddCityClick,
            modifier = Modifier.size(dimensionResource(R.dimen.home_top_bar_button_size))
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.home_add_city_button_description),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(R.dimen.home_top_bar_icon_size))
            )
        }
    }
}