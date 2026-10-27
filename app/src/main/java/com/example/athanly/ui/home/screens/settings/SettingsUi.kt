package com.example.athanly.ui.home.screens.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    onLanguageRowClick: () -> Unit,
    onMuadhdhinRowClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.settingsState.collectAsStateWithLifecycle()

    // App-specific notification settings live in the OS, not this app — no in-app screen
    // or DataStore state needed, just hand off to Android's own settings UI.
    val context = LocalContext.current
    val onNotificationSettingsRowClick = {
        context.startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        )
    }

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            SettingsUiExpanded(
                state = state,
                onLanguageRowClick = onLanguageRowClick,
                onTimeFormatToggled = viewModel::onTimeFormatToggled,
                onAsrCalculationToggled = viewModel::onHanafiModeToggled,
                onMuadhdhinRowClick = onMuadhdhinRowClick,
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
                onNotificationSettingsRowClick = onNotificationSettingsRowClick,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
    }
}
