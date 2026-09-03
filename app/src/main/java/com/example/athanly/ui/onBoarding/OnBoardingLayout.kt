package com.example.athanly.ui.onBoarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.athanly.ui._navigation.navGraphs.onBoardingNavGraph
import com.example.athanly.ui._navigation.routes.OnBoardingRoutes
import com.example.athanly.ui.onBoarding.onBoardingSubScreens.OnBoardingBottomBar
import com.example.athanly.ui.onBoarding.onBoardingSubScreens.OnBoardingTopBar
import com.example.athanly.ui.onBoarding.viewModel.OnBoardingViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// Shared with OnBoardingNavGraph's enter/exit transitions — keeping one source of truth
// means the bar/padding delay below always matches how long the screen animation actually takes
const val OnBoardingBarTransitionDurationMillis = 500

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

    // Separate instance from whatever the Location screen itself holds (hiltViewModel() is
    // scoped per nav destination) — only used here to gate Continue below, reading
    // hasSavedCity off Room via observeCity() so both instances agree regardless of which
    // one actually performed the save (see OnBoardingViewModel)
    val viewModel: OnBoardingViewModel = hiltViewModel()
    val locationState by viewModel.locationState.collectAsStateWithLifecycle()

    // Ordered list used to derive the current step index from the active destination
    val steps = listOf(
        OnBoardingRoutes.Welcome,
        OnBoardingRoutes.AthanSelection,
        OnBoardingRoutes.Location,
        OnBoardingRoutes.Calculations,
        OnBoardingRoutes.Notifications
    )

    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()

    // Map the active destination to a 0-indexed step — falls back to 0 if no match
    val currentStep = steps.indexOfFirst { route ->
        currentBackStackEntry?.destination?.hasRoute(route::class) == true
    }.coerceAtLeast(0)

    // currentBackStackEntry flips to the new destination the instant navigate() is called —
    // well before its enter/exit transition finishes playing. Driving bars/padding off it directly
    // would collapse them mid-animation and snap the still-visible outgoing screen to the edges.
    // displayedDestination instead lags behind by the transition duration, so bars/padding only
    // change once the screen swap has actually settled.
    var displayedDestination by remember { mutableStateOf(currentBackStackEntry?.destination) }

    LaunchedEffect(currentBackStackEntry) {
        delay(OnBoardingBarTransitionDurationMillis.toLong().milliseconds)

        displayedDestination = currentBackStackEntry?.destination
    }

    // Delayed — gates the bars' reserved layout space, so it only collapses once the
    // outgoing screen has actually finished animating off (see displayedDestination above)
    val isFullBleedStep = displayedDestination?.let { destination ->
        destination.hasRoute<OnBoardingRoutes.Notifications>() || destination.hasRoute<OnBoardingRoutes.Greeting>()
    } == true

    // Immediate — drives the bars' own opacity so they start fading out the instant navigation
    // begins instead of sitting fully opaque for the whole delay and then popping away
    val targetIsFullBleed = currentBackStackEntry?.destination?.let { destination ->
        destination.hasRoute<OnBoardingRoutes.Notifications>() || destination.hasRoute<OnBoardingRoutes.Greeting>()
    } == true

    Scaffold(
        topBar = {
            if (!isFullBleedStep)
                AnimatedVisibility(
                    visible = !targetIsFullBleed,
                    enter = fadeIn(tween(OnBoardingBarTransitionDurationMillis)),
                    exit = fadeOut(tween(OnBoardingBarTransitionDurationMillis))
                ) {
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
                            if(currentStep < steps.size - 1) {
                                OnBoardingTopBar(
                                    currentStep = currentStep,
                                    totalSteps = steps.size - 1
                                )
                            }
                        }
                    )
                }
        },
        bottomBar = {
            if (!isFullBleedStep)
                AnimatedVisibility(
                    visible = !targetIsFullBleed,
                    enter = fadeIn(tween(OnBoardingBarTransitionDurationMillis)),
                    exit = fadeOut(tween(OnBoardingBarTransitionDurationMillis))
                ) {
                    OnBoardingBottomBar(
                        onContinueClick = {
                            when (steps.getOrNull(currentStep + 1)) {
                                OnBoardingRoutes.AthanSelection -> innerNavController.navigate(OnBoardingRoutes.AthanSelection)
                                OnBoardingRoutes.Location -> innerNavController.navigate(OnBoardingRoutes.Location)
                                OnBoardingRoutes.Calculations -> innerNavController.navigate(OnBoardingRoutes.Calculations)
                                OnBoardingRoutes.Notifications -> innerNavController.navigate(OnBoardingRoutes.Notifications)
                                else -> onBoardingIsComplete()
                            }
                        },
                        // Only the Location step requires state before advancing — every
                        // other step's Continue stays enabled as before
                        enabled = currentBackStackEntry?.destination?.hasRoute<OnBoardingRoutes.Location>() != true ||
                            locationState.hasSavedCity
                    )
                }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // No shared padding here anymore — Calculations/AthanSelection/Welcome and
            // Notifications/Greeting each apply their own correct padding inside the nav graph,
            // since NavHost renders both the outgoing and incoming screen at once during a
            // transition and one shared padding value can't be right for both simultaneously
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = innerNavController,
                startDestination = OnBoardingRoutes.Welcome
            ) {
                onBoardingNavGraph(
                    currentScreen = windowSizeClass,
                    barPadding = innerPadding,
                    // Notifications hands off to the Greeting interstitial, not straight to Home
                    onNotificationsStepComplete = {
                        innerNavController.navigate(OnBoardingRoutes.Greeting)
                    },
                    onBoardingIsComplete = onBoardingIsComplete
                )
            }
        }
    }
}