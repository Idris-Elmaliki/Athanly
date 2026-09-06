package com.example.athanly.ui.home.screens.home.homeSubScreens.changeCity

import android.Manifest
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.home.viewModel.HomeViewModel

// Parent dispatcher for the ChangeCity screen, pushed from Home's top bar. Reuses HomeViewModel
// rather than a dedicated ViewModel — same reasoning as MuadhdhinPickerUi reusing
// SettingsViewModel, since locationState/onLocationPermissionResult/onPresetCitySelected
// already live there.
@Composable
fun ChangeCityUi(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    topBarPadding: Dp,
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.locationState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Fine and coarse are requested together — a "granted" result from either is enough to
    // attempt a GPS fix, same as onboarding's Location step
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { grantResults ->
        val isGranted = grantResults.values.any { it }
        viewModel.onLocationPermissionResult(isGranted)
    }

    val onShareLocationClick: () -> Unit = {
        val locationManager = context.getSystemService(LocationManager::class.java)
        if (locationManager?.isLocationEnabled == true) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        } else {
            // Device-level Location Services are off entirely — no permission dialog would
            // help here, so this deep-links straight to the system screen where the user
            // actually flips that toggle, then returns to the app on their own
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            ChangeCityUiExpanded(
                state = state,
                onShareLocationClick = onShareLocationClick,
                onPresetCitySelected = viewModel::onPresetCitySelected,
                onBackClick = onBackClick,
                topBarPadding = topBarPadding,
                modifier = modifier,
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            ChangeCityUiMedium(
                state = state,
                onShareLocationClick = onShareLocationClick,
                onPresetCitySelected = viewModel::onPresetCitySelected,
                onBackClick = onBackClick,
                topBarPadding = topBarPadding,
                modifier = modifier,
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            ChangeCityUiCompact(
                state = state,
                onShareLocationClick = onShareLocationClick,
                onPresetCitySelected = viewModel::onPresetCitySelected,
                onBackClick = onBackClick,
                topBarPadding = topBarPadding,
                modifier = modifier,
            )
        }
    }
}