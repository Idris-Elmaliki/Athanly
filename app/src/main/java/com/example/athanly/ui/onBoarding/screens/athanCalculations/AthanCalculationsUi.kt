package com.example.athanly.ui.onBoarding.screens.athanCalculations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.onBoarding.screens.athanCalculations.compact.CalculationsUiCompact
import com.example.athanly.ui.onBoarding.screens.athanCalculations.expanded.CalculationsUiExpanded
import com.example.athanly.ui.onBoarding.screens.athanCalculations.medium.CalculationsUiMedium
import com.example.athanly.ui.onBoarding.viewModel.OnBoardingViewModel

@Composable
fun CalculationsUi(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    viewModel: OnBoardingViewModel = hiltViewModel(),
) {
    val state by viewModel.calculationsState.collectAsStateWithLifecycle()

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            CalculationsUiExpanded(
                state = state,
                onHanafiModeToggled = viewModel::onHanafiModeToggled,
                modifier = modifier,
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            CalculationsUiMedium(
                state = state,
                onHanafiModeToggled = viewModel::onHanafiModeToggled,
                modifier = modifier,
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            CalculationsUiCompact(
                state = state,
                onHanafiModeToggled = viewModel::onHanafiModeToggled,
                modifier = modifier,
            )
        }
    }
}