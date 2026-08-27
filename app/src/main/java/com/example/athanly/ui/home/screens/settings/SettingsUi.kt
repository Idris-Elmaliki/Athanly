package com.example.athanly.ui.home.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.home.screens.settings.compact.SettingsUiCompact
import com.example.athanly.ui.home.screens.settings.expanded.SettingsUiExpanded
import com.example.athanly.ui.home.screens.settings.medium.SettingsUiMedium
import com.example.athanly.ui.home.viewModel.SettingsViewModel

@Composable
fun SettingsUi(
    modifier: Modifier,
    windowSizeClass: WindowSizeClass,
    topBarPadding: Dp,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.settingsState.collectAsStateWithLifecycle()

    // TODO: wire to a language picker screen once it's built (separate future plan)
    val onLanguageRowClick = {}
    // TODO: wire to a Muadhdhin picker screen once it's built (separate future plan)
    val onMuadhdhinRowClick = {}
    // TODO: wire to a notification settings screen once it's built (separate future plan)
    val onNotificationSettingsRowClick = {}

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            SettingsUiExpanded(
                state = state,
                onLanguageRowClick = onLanguageRowClick,
                onTimeFormatToggled = viewModel::onTimeFormatToggled,
                onAsrCalculationToggled = viewModel::onHanafiModeToggled,
                onMuadhdhinRowClick = onMuadhdhinRowClick,
                onVolumeChanged = viewModel::onAthanVolumeChanged,
                onNotificationSettingsRowClick = onNotificationSettingsRowClick,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            SettingsUiMedium(
                state = state,
                onLanguageRowClick = onLanguageRowClick,
                onTimeFormatToggled = viewModel::onTimeFormatToggled,
                onAsrCalculationToggled = viewModel::onHanafiModeToggled,
                onMuadhdhinRowClick = onMuadhdhinRowClick,
                onVolumeChanged = viewModel::onAthanVolumeChanged,
                onNotificationSettingsRowClick = onNotificationSettingsRowClick,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            SettingsUiCompact(
                state = state,
                onLanguageRowClick = onLanguageRowClick,
                onTimeFormatToggled = viewModel::onTimeFormatToggled,
                onAsrCalculationToggled = viewModel::onHanafiModeToggled,
                onMuadhdhinRowClick = onMuadhdhinRowClick,
                onVolumeChanged = viewModel::onAthanVolumeChanged,
                onNotificationSettingsRowClick = onNotificationSettingsRowClick,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
    }
}
