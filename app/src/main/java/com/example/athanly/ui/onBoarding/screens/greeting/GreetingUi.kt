package com.example.athanly.ui.onBoarding.screens.greeting

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.onBoarding.screens.greeting.compact.GreetingUiCompact
import com.example.athanly.ui.onBoarding.screens.greeting.expanded.GreetingUiExpanded
import com.example.athanly.ui.onBoarding.screens.greeting.medium.GreetingUiMedium
import com.example.athanly.ui.onBoarding.viewModel.OnBoardingViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// Animation state stays local (no persisted state of its own) — but the fade sequence's
// completion is also the moment onboarding is marked done, via viewModel.onOnboardingComplete()
private const val FADE_DURATION_MS = 850
private const val HOLD_AFTER_FADE_MS = 2000L

@Composable
fun GreetingUi(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    onBoardingIsComplete: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    val salamAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    // Sequences the two fades, persists onboarding-complete, then hands off to Home
    // once the hold period elapses
    LaunchedEffect(Unit) {
        salamAlpha.animateTo(1f, animationSpec = tween(FADE_DURATION_MS))
        subtitleAlpha.animateTo(1f, animationSpec = tween(FADE_DURATION_MS))
        delay(HOLD_AFTER_FADE_MS.milliseconds)
        viewModel.onOnboardingComplete()
        onBoardingIsComplete()
    }

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            GreetingUiExpanded(
                salamAlpha = salamAlpha.value,
                subtitleAlpha = subtitleAlpha.value,
                modifier = modifier
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            GreetingUiMedium(
                salamAlpha = salamAlpha.value,
                subtitleAlpha = subtitleAlpha.value,
                modifier = modifier
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            GreetingUiCompact(
                salamAlpha = salamAlpha.value,
                subtitleAlpha = subtitleAlpha.value,
                modifier = modifier
            )
        }
    }
}