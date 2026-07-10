package com.example.athanly.ui.subscreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.athanly.R
import com.example.athanly.ui._navigation.navData.BottomNavItem
import com.example.athanly.ui._navigation.navData.bottomNavItems
import com.example.athanly.ui._navigation.routes.MainRoutes

@Composable
fun BottomNavBar(
    currentRoute: MainRoutes,
    onTabSelected: (MainRoutes) -> Unit,
    modifier: Modifier = Modifier
) {
    // Rounded top corners + shadow separate the bar from the swipeable content behind it
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.bottom_nav_height)),
        shape = RoundedCornerShape(
            topStart = dimensionResource(R.dimen.bottom_nav_top_corner_radius),
            topEnd = dimensionResource(R.dimen.bottom_nav_top_corner_radius)
        ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = dimensionResource(R.dimen.bottom_nav_elevation)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            bottomNavItems.forEach { item ->
                BottomNavTab(
                    item = item,
                    isActive = item.route == currentRoute,
                    onClick = { onTabSelected(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavTab(
    item: BottomNavItem,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Active/inactive is conveyed by color alone (tertiary = Yellow) — same convention
    // already used for active card borders/radio buttons elsewhere in the app
    val tint = if (isActive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
    val label = stringResource(item.labelRes)

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(R.dimen.bottom_nav_item_vertical_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(dimensionResource(R.dimen.bottom_nav_icon_size))
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.bottom_nav_icon_label_gap)))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
    }
}