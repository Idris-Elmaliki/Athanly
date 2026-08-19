package com.example.athanly.ui.home.screens.qibla

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.home.screens.qibla.compact.QiblaUiCompact
import com.example.athanly.ui.home.screens.qibla.expanded.QiblaUiExpanded
import com.example.athanly.ui.home.screens.qibla.medium.QiblaUiMedium
import com.example.athanly.ui.home.viewModel.QiblaViewModel

@Composable
fun QiblaUi(
    modifier: Modifier,
    windowSizeClass: WindowSizeClass,
    topBarPadding: Dp,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val state by viewModel.qiblaState.collectAsStateWithLifecycle()

    // TODO: wire to the nav drawer once it's built (separate future plan) — matches HomeUi's onMenuClick
    val onMenuClick = {}

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            QiblaUiExpanded(
                state = state,
                onMenuClick = onMenuClick,
                onUnitToggled = viewModel::onDistanceUnitToggled,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            QiblaUiMedium(
                state = state,
                onMenuClick = onMenuClick,
                onUnitToggled = viewModel::onDistanceUnitToggled,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            QiblaUiCompact(
                state = state,
                onMenuClick = onMenuClick,
                onUnitToggled = viewModel::onDistanceUnitToggled,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
    }
}