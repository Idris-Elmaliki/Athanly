package com.example.athanly.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.athanly.ui._navigation.navGraphs.mainNavGraph
import com.example.athanly.ui._navigation.routes.MainRoutes
import com.example.athanly.ui.subscreens.BottomNavBar

// Persistent scaffold for the post-onboarding app shell — owns the inner NavHost so the
// bottom nav bar doesn't recompose when switching between Home/Qibla/Settings
@Composable
fun HomeLayout(
    modifier: Modifier
) {
    val innerNavController = rememberNavController()

    // Resolves the current window size class reactively — updates on rotation or multi-window resize
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()

    // Map the active destination to its MainRoutes tab — falls back to Home if no match yet
    val currentRoute = when {
        currentBackStackEntry?.destination?.hasRoute<MainRoutes.Qibla>() == true -> MainRoutes.Qibla
        currentBackStackEntry?.destination?.hasRoute<MainRoutes.Settings>() == true -> MainRoutes.Settings
        else -> MainRoutes.Home
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onTabSelected = { route ->
                    // Standard bottom-nav pattern: avoid piling up duplicate destinations on the
                    // back stack, and restore each tab's own state when switching back to it
                    innerNavController.navigate(route) {
                        popUpTo(innerNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                navController = innerNavController,
                startDestination = MainRoutes.Home
            ) {
                // Only HomeTopBar needs this — applying it to the whole NavHost would push
                // AtmosphericBackground down from the top, exposing a plain white strip
                // behind the status bar instead of staying edge-to-edge
                mainNavGraph(
                    currentScreen = windowSizeClass,
                    topBarPadding = (innerPadding.calculateTopPadding() / 2)
                )
            }
        }
    }
}
