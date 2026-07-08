package com.example.athanly.ui._navigation.navGraphs

import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui._navigation.routes.OnBoardingRoutes
import com.example.athanly.ui.onBoarding.screens.athanSelection.AthanSelectionUi
import com.example.athanly.ui.onBoarding.screens.calculations.CalculationsUi
import com.example.athanly.ui.onBoarding.screens.greeting.GreetingUi
import com.example.athanly.ui.onBoarding.screens.notifications.NotificationsUi
import com.example.athanly.ui.onBoarding.screens.welcome.WelcomeUi

// Extension on NavGraphBuilder so AppRoute stays clean — each feature owns its own graph
fun NavGraphBuilder.onBoardingNavGraph(
    currentScreen : WindowSizeClass,
    onNotificationsStepComplete: () -> Unit,
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
    // After notifications, advance to the Greeting interstitial rather than completing onboarding directly
    composable<OnBoardingRoutes.Notifications> {
        NotificationsUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
            onNotificationsStepComplete = onNotificationsStepComplete,
        )
    }
    // Greeting is the true final step — once its fade sequence finishes, it pops the onboarding graph and lands on Home
    // No built-in enter transition — our own alpha fade is the only thing that should animate visibility here
    composable<OnBoardingRoutes.Greeting>(
        enterTransition = { 
            EnterTransition.None 
        }
    ) {
        GreetingUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
            onBoardingIsComplete = onBoardingIsComplete,
        )
    }
}