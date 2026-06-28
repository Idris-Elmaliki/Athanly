package com.example.athanly.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athanly.ui._navigation.navGraphs.onBoardingNavGraph
import com.example.athanly.ui._navigation.routes.AppRoutes

// Single Compose entry point for all navigation — called by MainActivity
@Composable
fun AppRoute(
    modifier: Modifier = Modifier
) {
    // NavController manages the back stack and destination state internally
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // First destination shown on launch — will be DataStore-driven (OnBoarding vs Home) once wired up
        startDestination = AppRoutes.OnBoarding,
        modifier = modifier
    ) {
        // Each feature graph is registered as an extension function to keep this file clean
        onBoardingNavGraph(navController)
        composable<AppRoutes.Home> {}
    }
}