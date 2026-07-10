package com.example.athanly.ui._navigation.navGraphs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.R
import com.example.athanly.ui._navigation.routes.MainRoutes

// Extension on NavGraphBuilder so MainLayout stays clean — each feature owns its own graph
fun NavGraphBuilder.mainNavGraph(
    currentScreen: WindowSizeClass
) {
    // TODO: swap for the real HomeUi once it's built later in this plan
    composable<MainRoutes.Home> {
        MainTabPlaceholder(textRes = R.string.main_home_placeholder)
    }
    // Qibla is a separate future plan — placeholder keeps the bottom nav fully navigable now
    composable<MainRoutes.Qibla> {
        MainTabPlaceholder(textRes = R.string.main_qibla_placeholder)
    }
    // Settings is a separate future plan — placeholder keeps the bottom nav fully navigable now
    composable<MainRoutes.Settings> {
        MainTabPlaceholder(textRes = R.string.main_settings_placeholder)
    }
}

@Composable
private fun MainTabPlaceholder(textRes: Int) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(textRes), style = MaterialTheme.typography.bodyLarge)
    }
}