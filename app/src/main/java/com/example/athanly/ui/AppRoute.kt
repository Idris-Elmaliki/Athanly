package com.example.athanly.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.athanly.ui._navigation.AppRoutes

// Single Compose entry point for all navigation — called by MainActivity
@Composable
fun AppRoute(
    modifier: Modifier
) {
    // Navigation 3's back stack — replaces NavController.
    // Starts at OnBoarding; will be DataStore-driven (OnBoarding vs Home) once that's wired up
    val backStack = remember { mutableListOf<AppRoutes>(AppRoutes.OnBoarding) }

    // Renders whichever destination is at the top of the back stack
    NavDisplay(
        backStack = backStack,
        // Guard: only pop when there's more than one destination to prevent exiting the root
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
    ) { route ->
        // NavEntry pairs a route key with its composable content — required by NavDisplay
        NavEntry(
            key = route
        ) {
            // Destinations will be registered here as screens are built
            when (route) {
                is AppRoutes.OnBoarding -> {}
                is AppRoutes.Home -> {}
            }
        }
    }
}