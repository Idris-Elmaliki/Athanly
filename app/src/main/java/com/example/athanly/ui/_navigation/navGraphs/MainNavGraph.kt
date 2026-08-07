package com.example.athanly.ui._navigation.navGraphs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.R
import com.example.athanly.ui._navigation.routes.MainRoutes
import com.example.athanly.ui.home.screens.home.HomeUi
import com.example.athanly.ui.qibla.screens.qibla.QiblaUi

// Extension on NavGraphBuilder so MainLayout stays clean — each feature owns its own graph
fun NavGraphBuilder.mainNavGraph(
    currentScreen: WindowSizeClass,
    topBarPadding: Dp
) {
    composable<MainRoutes.Home> {
        HomeUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
            topBarPadding = topBarPadding,
        )
    }
    composable<MainRoutes.Qibla> {
        QiblaUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
            topBarPadding = topBarPadding,
        )
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