package com.example.athanly.ui.onBoarding.screens.location

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.onBoarding.screens.location.compact.LocationUiCompact
import com.example.athanly.ui.onBoarding.screens.location.expanded.LocationUiExpanded
import com.example.athanly.ui.onBoarding.screens.location.medium.LocationUiMedium
import com.example.athanly.ui.onBoarding.viewModel.OnBoardingViewModel

@Composable
fun LocationUi(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    viewModel: OnBoardingViewModel = hiltViewModel(),
) {
    val state by viewModel.locationState.collectAsStateWithLifecycle()

    // Fine and coarse are requested together — a "granted" result from either is enough to
    // attempt a GPS fix, since FusedLocationProviderClient works with either permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { grantResults ->
        val isGranted = grantResults.values.any { it }
        viewModel.onLocationPermissionResult(isGranted)
    }

    val onShareLocationClick: () -> Unit = {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            LocationUiExpanded(
                state = state,
                onShareLocationClick = onShareLocationClick,
                onPresetCitySelected = viewModel::onPresetCitySelected,
                undoPresetCitySelected = viewModel::undoPresetCitySelected,
                modifier = modifier,
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            LocationUiMedium(
                state = state,
                onShareLocationClick = onShareLocationClick,
                onPresetCitySelected = viewModel::onPresetCitySelected,
                undoPresetCitySelected = viewModel::undoPresetCitySelected,
                modifier = modifier,
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            LocationUiCompact(
                state = state,
                onShareLocationClick = onShareLocationClick,
                onPresetCitySelected = viewModel::onPresetCitySelected,
                undoPresetCitySelected = viewModel::undoPresetCitySelected,
                modifier = modifier,
            )
        }
    }
}