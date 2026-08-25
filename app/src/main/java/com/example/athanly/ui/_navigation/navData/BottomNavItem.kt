package com.example.athanly.ui._navigation.navData

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.athanly.R
import com.example.athanly.ui._navigation.routes.MainRoutes

// Icon + label spec for one BottomNavBar tab — pairs a MainRoutes destination with its display data
data class BottomNavItem(
    val route: MainRoutes,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val labelRes: Int
)

// The 3 bottom nav tabs, in display order — drives the BottomNavBar row-building loop
val bottomNavItems = listOf(
    BottomNavItem(
        route = MainRoutes.Home,
        filledIcon = Icons.Filled.Home,
        outlinedIcon = Icons.Outlined.Home,
        labelRes = R.string.bottom_nav_home
    ),
    BottomNavItem(
        route = MainRoutes.Qibla,
        filledIcon = Icons.Filled.Explore,
        outlinedIcon = Icons.Outlined.Explore,
        labelRes = R.string.bottom_nav_qibla
    ),
    BottomNavItem(
        route = MainRoutes.Settings,
        filledIcon = Icons.Filled.Settings,
        outlinedIcon = Icons.Outlined.Settings,
        labelRes = R.string.bottom_nav_settings
    ),
)