package com.example.athanly.ui.onBoarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingLayout(
    modifier: Modifier,
    onBoardingIsComplete: () -> Unit
) {
    val innerNavController = rememberNavController()

    // Resolves the current window size class reactively — updates on rotation or multi-window resize
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

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
            CenterAlignedTopAppBar(
                // Back button hidden on the first step — nowhere to go back to
                navigationIcon = {
                    if (currentStep > 0) {
                        IconButton(onClick = { innerNavController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
                title = {
                    if (currentBackStackEntry?.destination != OnBoardingRoutes.Notifications)
                        OnBoardingTopBar(
                            currentStep = currentStep,
                            totalSteps = steps.size - 1
                        )
                }
            )
        },
        bottomBar = {
            if (currentBackStackEntry?.destination != OnBoardingRoutes.Notifications)
                OnBoardingBottomBar(
                    onContinueClick = {
                        when (steps.getOrNull(currentStep + 1)) {
                            OnBoardingRoutes.AthanSelection -> innerNavController.navigate(OnBoardingRoutes.AthanSelection)
                            OnBoardingRoutes.Calculations -> innerNavController.navigate(OnBoardingRoutes.Calculations)
                            OnBoardingRoutes.Notifications -> innerNavController.navigate(OnBoardingRoutes.Notifications)
                            else -> onBoardingIsComplete()
                        }
                    }
                )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient lives here so it doesn't inflate innerPadding.calculateTopPadding()
            // to 35% of screen height. Scaffold content starts at y=0 (behind TopAppBar),
            // so the gradient still visually covers the bar area from the top.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.35f)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                navController = innerNavController,
                startDestination = OnBoardingRoutes.Welcome
            ) {
                onBoardingNavGraph(
                    navController = innerNavController,
                    currentScreen = windowSizeClass,
                    onBoardingIsComplete = onBoardingIsComplete
                )
            }
        }
    }
}