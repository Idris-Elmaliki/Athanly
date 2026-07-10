package com.example.athanly.ui._navigation.navData

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.athanly.R
import com.example.athanly.ui._navigation.routes.MainRoutes

// Icon + label spec for one BottomNavBar tab — pairs a MainRoutes destination with its display data
data class BottomNavItem(
    val route: MainRoutes,
    val icon: ImageVector,
    val labelRes: Int
)

// The 3 bottom nav tabs, in display order — drives the BottomNavBar row-building loop
val bottomNavItems = listOf(
    BottomNavItem(route = MainRoutes.Home, icon = Icons.Filled.Home, labelRes = R.string.bottom_nav_home),
    BottomNavItem(route = MainRoutes.Qibla, icon = Icons.Filled.Explore, labelRes = R.string.bottom_nav_qibla),
    BottomNavItem(route = MainRoutes.Settings, icon = Icons.Filled.Settings, labelRes = R.string.bottom_nav_settings),
)