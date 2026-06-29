package com.example.athanly.ui.onBoarding

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.athanly.ui._navigation.navGraphs.onBoardingNavGraph
import com.example.athanly.ui._navigation.routes.OnBoardingRoutes
import com.example.athanly.ui.onBoarding.onBoardingSubScreens.OnBoardingBottomBar
import com.example.athanly.ui.onBoarding.onBoardingSubScreens.OnBoardingTopBar

// Persistent scaffold for all onboarding steps — owns the inner NavHost so the
// progress indicator and continue button don't recompose on every step navigation
@Composable
fun OnBoardingLayout(
    onBoardingIsComplete: () -> Unit
) {
    val innerNavController = rememberNavController()

    // Ordered list used to derive the current step index from the active destination
    val steps = listOf(
        OnBoardingRoutes.Welcome,
        OnBoardingRoutes.AthanSelection,
        OnBoardingRoutes.Calculations,
        OnBoardingRoutes.Notifications
    )

    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()

    // Map the active destination to a 0-indexed step — falls back to 0 if no match
    val currentStep = steps.indexOfFirst { route ->
        currentBackStackEntry?.destination?.hasRoute(route::class) == true
    }.coerceAtLeast(0)

    Scaffold(
        topBar = {
            if(currentBackStackEntry?.destination != OnBoardingRoutes.Notifications)
                OnBoardingTopBar(
                    currentStep = currentStep,
                    totalSteps = steps.size - 1 // we don't want to include the notification page into the pagination system
                )
        },
        bottomBar = {
            if(currentBackStackEntry?.destination != OnBoardingRoutes.Notifications)
                OnBoardingBottomBar(
                    // Navigate forward on each step; the last step calls onBoardingIsComplete
                    onContinueClick = {
                        when (steps.getOrNull(currentStep + 1)) {
                            OnBoardingRoutes.AthanSelection -> innerNavController.navigate(OnBoardingRoutes.AthanSelection)
                            OnBoardingRoutes.Calculations -> innerNavController.navigate(OnBoardingRoutes.Calculations)
                            OnBoardingRoutes.Notifications -> innerNavController.navigate(OnBoardingRoutes.Notifications)
                            // No next step — onboarding is done
                            else -> onBoardingIsComplete()
                        }
                    }
                )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = innerNavController,
            startDestination = OnBoardingRoutes.Welcome
        ) {
            onBoardingNavGraph(
                navController = innerNavController,
                onBoardingIsComplete = onBoardingIsComplete
            )
        }
    }
}