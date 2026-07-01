package com.example.athanly.ui.onBoarding.screens.athanSelection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.onBoarding.screens.athanSelection.compact.AthanSelectionUiCompact
import com.example.athanly.ui.onBoarding.screens.athanSelection.expanded.AthanSelectionUiExpanded
import com.example.athanly.ui.onBoarding.screens.athanSelection.medium.AthanSelectionUiMedium
import com.example.athanly.ui.onBoarding.viewModel.OnBoardingViewModel

@Composable
fun AthanSelectionUi(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    viewModel: OnBoardingViewModel = hiltViewModel(),
) {
    val state by viewModel.athanSelectionState.collectAsStateWithLifecycle()

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            AthanSelectionUiExpanded(
                state = state,
                onMuadhhinSelected = viewModel::onMuadhhinSelected,
                modifier = modifier
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            AthanSelectionUiMedium(
                state = state,
                onMuadhhinSelected = viewModel::onMuadhhinSelected,
                modifier = modifier
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            AthanSelectionUiCompact(
                state = state,
                onMuadhhinSelected = viewModel::onMuadhhinSelected,
                modifier = modifier
            )
        }
    }
}