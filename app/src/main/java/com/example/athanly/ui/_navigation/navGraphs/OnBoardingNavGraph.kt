package com.example.athanly.ui._navigation.navGraphs

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui._navigation.routes.OnBoardingRoutes
import com.example.athanly.ui.onBoarding.OnBoardingBarTransitionDurationMillis
import com.example.athanly.ui.onBoarding.screens.athanSelection.AthanSelectionUi
import com.example.athanly.ui.onBoarding.screens.athanCalculations.CalculationsUi
import com.example.athanly.ui.onBoarding.screens.greeting.GreetingUi
import com.example.athanly.ui.onBoarding.screens.notifications.NotificationsUi
import com.example.athanly.ui.onBoarding.screens.welcome.WelcomeUi

// Duration matches OnBoardingBarTransitionDurationMillis so OnBoardingLayout's delayed
// bar/padding swap always lines up with how long the on-screen transition actually takes
private val onBoardingTransitionOut = fadeOut(tween(OnBoardingBarTransitionDurationMillis))

// Extension on NavGraphBuilder so AppRoute stays clean — each feature owns its own graph
fun NavGraphBuilder.onBoardingNavGraph(
    currentScreen : WindowSizeClass,
    // Only applied to the bar-having screens below — Notifications/Greeting are full-bleed
    // and always ignore it, so they never wait on OnBoardingLayout's delayed bar collapse
    barPadding: PaddingValues,
    onNotificationsStepComplete: () -> Unit,
    onBoardingIsComplete: () -> Unit
) {
    composable<OnBoardingRoutes.Welcome>(
        exitTransition = { onBoardingTransitionOut }
    ) {
        WelcomeUi(
            modifier = Modifier
                .fillMaxSize()
                .padding(barPadding),
            windowSizeClass = currentScreen,
        )
    }
    composable<OnBoardingRoutes.AthanSelection> {
        AthanSelectionUi(
            modifier = Modifier
                .fillMaxSize()
                .padding(barPadding),
            windowSizeClass = currentScreen,
        )
    }
    composable<OnBoardingRoutes.Calculations>(
        exitTransition = { onBoardingTransitionOut }
    ) {
        CalculationsUi(
            modifier = Modifier
                .fillMaxSize()
                .padding(barPadding),
            windowSizeClass = currentScreen,
        )
    }
    // After notifications, advance to the Greeting interstitial rather than completing onboarding directly
    composable<OnBoardingRoutes.Notifications>(
        // Slides in from the right instead of fading, so it reads as a forward "push" rather
        // than a cross-dissolve — same duration as the rest so the delayed bar/padding swap still lines up
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(OnBoardingBarTransitionDurationMillis)
            )
        }
    ) {
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