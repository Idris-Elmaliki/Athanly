package com.example.athanly.ui.onBoarding

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.athanly.ui._navigation.navGraphs.onBoardingNavGraph
import com.example.athanly.ui._navigation.routes.OnBoardingRoutes

// Persistent scaffold for all onboarding steps — owns the inner NavHost so the
// progress indicator and continue button don't recompose on every step change
@Composable
fun OnBoardingLayout(
    // Callback invoked when the user finishes the last onboarding step
    onBoardingIsComplete: () -> Unit
) {
    // Inner NavController scoped only to onboarding — isolated from the app-level NavController
    val innerNavController = rememberNavController()

    Scaffold(
        // Progress indicator dots live here — persistent across all onboarding steps
        topBar = {},
        // Continue button lives here — persistent across all onboarding steps
        bottomBar = {}
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .padding(innerPadding),
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