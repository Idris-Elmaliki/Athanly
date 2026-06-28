package com.example.athanly.ui._navigation.navGraphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.athanly.ui._navigation.routes.AppRoutes
import com.example.athanly.ui._navigation.routes.OnBoardingRoutes

// Extension on NavGraphBuilder so AppRoute stays clean — each feature owns its own graph
fun NavGraphBuilder.onBoardingNavGraph(
    navController: NavController
) {
    // Nested graph scoped to AppRoutes.OnBoarding — isolates all onboarding destinations
    navigation<AppRoutes.OnBoarding>(
        startDestination = OnBoardingRoutes.Welcome
    ) {
        composable<OnBoardingRoutes.Welcome> {}
        composable<OnBoardingRoutes.AthanSelection> {}
        composable<OnBoardingRoutes.Calculations> {}
        // After notifications, pop the entire onboarding graph and land on Home
        composable<OnBoardingRoutes.Notifications> {}
    }
}