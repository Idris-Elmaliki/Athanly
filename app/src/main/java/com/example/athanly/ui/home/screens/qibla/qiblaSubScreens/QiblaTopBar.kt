package com.example.athanly.ui.home.screens.qibla.qiblaSubScreens

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

// Own file rather than reusing HomeTopBar — HomeTopBar is scoped to Home (its right-side
// button is "add city", which has no Qibla equivalent), per the plan's note that top bar
// chrome lives inside each tab's own screen, not a shared composable.
@Composable
fun QiblaTopBar(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.qibla_top_bar_height))
            .padding(horizontal = dimensionResource(R.dimen.qibla_top_bar_horizontal_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .size(dimensionResource(R.dimen.qibla_top_bar_button_size))
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = stringResource(R.string.home_menu_button_description),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.qibla_top_bar_icon_size))
            )
        }

        Spacer(
            modifier = Modifier
                .weight(1f)
        )

        // Balances the hamburger button on the left so the title stays visually
        // centered, mirroring the equal-width button slot HomeTopBar has on its right.
        Box(modifier = Modifier
            .size(dimensionResource(R.dimen.qibla_top_bar_button_size))
        )
    }
}