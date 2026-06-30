package com.example.athanly.ui._navigation.navGraphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.athanly.ui._navigation.routes.OnBoardingRoutes
import com.example.athanly.ui.onBoarding.screens.welcome.WelcomeUi

// Extension on NavGraphBuilder so AppRoute stays clean — each feature owns its own graph
fun NavGraphBuilder.onBoardingNavGraph(
    navController: NavController,
    onBoardingIsComplete: () -> Unit
) {
    composable<OnBoardingRoutes.Welcome> {
        WelcomeUi()
    }
    // Remaining screens are not yet implemented
    composable<OnBoardingRoutes.AthanSelection> {}
    composable<OnBoardingRoutes.Calculations> {}
    // After notifications, pop the entire onboarding graph and land on Home
    composable<OnBoardingRoutes.Notifications> {}
}