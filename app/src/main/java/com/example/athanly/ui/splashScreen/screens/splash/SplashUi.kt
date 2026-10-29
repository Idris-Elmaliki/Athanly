package com.example.athanly.ui.splashScreen.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.splashScreen.screens.splash.compact.SplashUiCompact
import com.example.athanly.ui.splashScreen.screens.splash.expanded.SplashUiExpanded
import com.example.athanly.ui.splashScreen.screens.splash.medium.SplashUiMedium
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private const val NAME_ANIMATION_MILLIS = 1000

// Pause after the flag arrives so the finished lockup is seen before the screen changes
private const val HAND_OFF_DELAY_MILLIS = 500L

@Composable
fun SplashUi(
    windowSizeClass: WindowSizeClass,
    hasCompletedOnboarding: Boolean?,
    onFinished: (hasCompletedOnboarding: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Starts false so the first frame is just the icon, matching the OS splash exactly
    var nameVisible by remember { mutableStateOf(false) }
    var nameAnimationFinished by remember { mutableStateOf(false) }

    // Keeps the effect below calling the latest lambda without restarting its timer
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        nameVisible = true
        // AnimatedVisibility doesn't report completion, so wait out its known duration
        delay(NAME_ANIMATION_MILLIS.toLong().milliseconds)
        nameAnimationFinished = true
    }

    // Leaves only once BOTH the name is fully shown and the flag has resolved — this is what
    // stops a returning user from ever seeing onboarding while the flag is still loading
    LaunchedEffect(nameAnimationFinished, hasCompletedOnboarding) {
        val resolvedFlag = hasCompletedOnboarding ?: return@LaunchedEffect
        if (!nameAnimationFinished) return@LaunchedEffect

        delay(HAND_OFF_DELAY_MILLIS.milliseconds)
        currentOnFinished(resolvedFlag)
    }

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            SplashUiExpanded(
                nameVisible = nameVisible,
                nameAnimationMillis = NAME_ANIMATION_MILLIS,
                modifier = modifier
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            SplashUiMedium(
                nameVisible = nameVisible,
                nameAnimationMillis = NAME_ANIMATION_MILLIS,
                modifier = modifier
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            SplashUiCompact(
                nameVisible = nameVisible,
                nameAnimationMillis = NAME_ANIMATION_MILLIS,
                modifier = modifier
            )
        }
    }
}
