package com.example.athanly.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athanly.ui._navigation.routes.AppRoutes
import com.example.athanly.ui.home.HomeLayout
import com.example.athanly.ui.onBoarding.OnBoardingLayout
import com.example.athanly.ui.splash.screens.splash.SplashUi
import com.example.athanly.ui.viewModel.AppViewModel

// Single Compose entry point for all navigation — called by MainActivity
@Composable
fun AppRoute(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = hiltViewModel()
) {
    // NavController manages app-level destinations (OnBoarding, Home, etc.)
    val navController = rememberNavController()

    // null while the DataStore read is still in flight — the Splash holds until it resolves
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        // Always Splash — it picks Home or OnBoarding once the flag is known, so neither
        // is ever shown on a guess
        startDestination = AppRoutes.Splash,
        modifier = modifier
    ) {
        composable<AppRoutes.Splash> {
            SplashUi(
                windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
                hasCompletedOnboarding = hasCompletedOnboarding,
                onFinished = { completedOnboarding ->
                    navController.navigate(
                        if (completedOnboarding) AppRoutes.Home else AppRoutes.OnBoarding
                    ) {
                        // Splash off the back stack so Back never returns to it
                        popUpTo(AppRoutes.Splash) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // OnBoardingLayout owns its own inner NavHost and step routing.
        // Lambda keeps OnBoardingLayout decoupled from AppRoutes — it only needs to signal "done"
        composable<AppRoutes.OnBoarding> {
            OnBoardingLayout(
                modifier = Modifier.fillMaxSize(),
                onBoardingIsComplete = {
                    navController.navigate(AppRoutes.Home) {
                        // Clear onboarding off the back stack so the user can't navigate back into it
                        popUpTo(AppRoutes.OnBoarding) { inclusive = true }
                    }
                }
            )
        }
        // HomeLayout owns its own inner NavHost + bottom nav for the Home/Qibla/Settings tabs
        composable<AppRoutes.Home> {
            HomeLayout(modifier = Modifier.fillMaxSize())
        }
    }
}