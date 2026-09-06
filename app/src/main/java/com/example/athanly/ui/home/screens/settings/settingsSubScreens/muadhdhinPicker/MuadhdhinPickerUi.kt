package com.example.athanly.ui.home.screens.settings.settingsSubScreens.muadhdhinPicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.home.viewModel.subScreenVMs.MuadhhinViewModel

// Parent dispatcher for the Muadhdhin picker, pushed from the Settings screen's Selected
// Muadhin row. Uses its own MuadhhinViewModel (not SettingsViewModel) since deferred-save
// selection and preview playback are specific to this one screen.
@Composable
fun MuadhdhinPickerUi(
    modifier: Modifier,
    windowSizeClass: WindowSizeClass,
    topBarPadding: Dp,
    onBackClick: () -> Unit,
    viewModel: MuadhhinViewModel = hiltViewModel()
) {
    val state by viewModel.pickerState.collectAsStateWithLifecycle()

    // Fires on every way this screen can be left (back arrow, system back gesture, or
    // navigating elsewhere) — commits whatever pick is pending and stops any preview audio
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onMuadhdhinPickerBackClick()
            viewModel.stopPreview()
        }
    }

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            MuadhdhinPickerUiExpanded(
                selectedMuadhdhin = state.selectedMuadhdhin,
                previewingMuadhdhinId = state.previewingMuadhdhinId,
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
                previewingMuadhdhinId = state.previewingMuadhdhinId,
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
                previewingMuadhdhinId = state.previewingMuadhdhinId,
                onMuadhdhinSelected = viewModel::onMuadhdhinSelected,
                onBackClick = onBackClick,
                topBarPadding = topBarPadding,
                modifier = modifier
            )
        }
    }
}