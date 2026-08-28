package com.example.athanly.ui.home.screens.settings.settingsSubScreens.muadhdhinPicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.home.viewModel.SettingsViewModel

// Parent dispatcher for the Muadhdhin picker, pushed from the Settings screen's Selected
// Muadhin row. Reuses SettingsViewModel rather than a dedicated ViewModel —
// selectedMuadhdhin/onMuadhdhinSelected already exist there since Settings itself displays
// and edits the same DataStore-backed value.
@Composable
fun MuadhdhinPickerUi(
    modifier: Modifier,
    windowSizeClass: WindowSizeClass,
    topBarPadding: Dp,
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.settingsState.collectAsStateWithLifecycle()

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            MuadhdhinPickerUiExpanded(
                selectedMuadhdhin = state.selectedMuadhdhin,
                onMuadhdhinSelected = viewModel::onMuadhdhinSelected,
                onBackClick = onBackClick,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            MuadhdhinPickerUiMedium(
                selectedMuadhdhin = state.selectedMuadhdhin,
                onMuadhdhinSelected = viewModel::onMuadhdhinSelected,
                onBackClick = onBackClick,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            MuadhdhinPickerUiCompact(
                selectedMuadhdhin = state.selectedMuadhdhin,
                onMuadhdhinSelected = viewModel::onMuadhdhinSelected,
                onBackClick = onBackClick,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
    }
}