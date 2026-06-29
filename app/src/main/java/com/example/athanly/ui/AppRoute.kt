package com.example.athanly.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athanly.ui._navigation.routes.AppRoutes
import com.example.athanly.ui.onBoarding.OnBoardingLayout

// Single Compose entry point for all navigation — called by MainActivity
@Composable
fun AppRoute(
    modifier: Modifier = Modifier
) {
    // NavController manages app-level destinations (OnBoarding, Home, etc.)
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // First destination shown on launch — will be DataStore-driven (OnBoarding vs Home) once wired up
        startDestination = AppRoutes.OnBoarding,
        modifier = modifier
    ) {
        // OnBoardingLayout owns its own inner NavHost and step routing.
        // Lambda keeps OnBoardingLayout decoupled from AppRoutes — it only needs to signal "done"
        composable<AppRoutes.OnBoarding> {
            OnBoardingLayout(
                onBoardingIsComplete = {
                    navController.navigate(AppRoutes.Home) {
                        // Clear onboarding off the back stack so the user can't navigate back into it
                        popUpTo(AppRoutes.OnBoarding) { inclusive = true }
                    }
                }
            )
        }
        composable<AppRoutes.Home> {}
    }
}