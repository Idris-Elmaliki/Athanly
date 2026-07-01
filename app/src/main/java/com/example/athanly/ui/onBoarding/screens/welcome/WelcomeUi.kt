package com.example.athanly.ui.onBoarding.screens.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.onBoarding.screens.welcome.compact.WelcomeUiCompact
import com.example.athanly.ui.onBoarding.screens.welcome.expanded.WelcomeUiExpanded
import com.example.athanly.ui.onBoarding.screens.welcome.medium.WelcomeUiMedium
import com.example.athanly.ui.onBoarding.viewModel.OnBoardingViewModel

@Composable
fun WelcomeUi(
    modifier: Modifier,
    windowSizeClass: WindowSizeClass,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    val state by viewModel.welcomeState.collectAsStateWithLifecycle()

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            WelcomeUiExpanded(
                state = state,
                onLanguageSelected = viewModel::onLanguageSelected,
                modifier = modifier
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            WelcomeUiMedium(
                state = state,
                onLanguageSelected = viewModel::onLanguageSelected,
                modifier = modifier
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            WelcomeUiCompact(
                state = state,
                onLanguageSelected = viewModel::onLanguageSelected,
                modifier = modifier
            )
        }
    }
}