package com.example.athanly.ui._navigation.navGraphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui._navigation.routes.OnBoardingRoutes
import com.example.athanly.ui.onBoarding.screens.athanSelection.AthanSelectionUi
import com.example.athanly.ui.onBoarding.screens.calculations.CalculationsUi
import com.example.athanly.ui.onBoarding.screens.notifications.NotificationsUi
import com.example.athanly.ui.onBoarding.screens.welcome.WelcomeUi

// Extension on NavGraphBuilder so AppRoute stays clean — each feature owns its own graph
fun NavGraphBuilder.onBoardingNavGraph(
    currentScreen : WindowSizeClass,
    onBoardingIsComplete: () -> Unit
) {
    composable<OnBoardingRoutes.Welcome> {
        WelcomeUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
        )
    }
    composable<OnBoardingRoutes.AthanSelection> {
        AthanSelectionUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
        )
    }
    composable<OnBoardingRoutes.Calculations> {
        CalculationsUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
        )
    }
    // After notifications, pop the entire onboarding graph and land on Home
    composable<OnBoardingRoutes.Notifications> {
        NotificationsUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
            onBoardingIsComplete = onBoardingIsComplete,
        )
    }
}