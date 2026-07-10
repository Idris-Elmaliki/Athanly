package com.example.athanly.ui.home.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.ui.unit.Dp
import com.example.athanly.ui.home.screens.home.compact.HomeUiCompact
import com.example.athanly.ui.home.screens.home.expanded.HomeUiExpanded
import com.example.athanly.ui.home.screens.home.medium.HomeUiMedium
import com.example.athanly.ui.home.viewModel.HomeViewModel

@Composable
fun HomeUi(
    modifier: Modifier,
    windowSizeClass: WindowSizeClass,
    topBarPadding: Dp,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsStateWithLifecycle()

    // TODO: wire to the nav drawer once it's built (separate future plan)
    val onMenuClick = {}
    // TODO: wire to the add-city flow once it's built (separate future plan)
    val onAddCityClick = {}

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            HomeUiExpanded(
                state = state,
                onMenuClick = onMenuClick,
                onAddCityClick = onAddCityClick,
                onCitySelected = viewModel::onCitySelected,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            HomeUiMedium(
                state = state,
                onMenuClick = onMenuClick,
                onAddCityClick = onAddCityClick,
                onCitySelected = viewModel::onCitySelected,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            HomeUiCompact(
                state = state,
                onMenuClick = onMenuClick,
                onAddCityClick = onAddCityClick,
                onCitySelected = viewModel::onCitySelected,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
    }
}