package com.example.athanly.ui._navigation.navGraphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui._navigation.routes.MainRoutes
import com.example.athanly.ui.home.screens.home.HomeUi
import com.example.athanly.ui.home.screens.qibla.QiblaUi
import com.example.athanly.ui.home.screens.settings.SettingsUi

// Extension on NavGraphBuilder so MainLayout stays clean — each feature owns its own graph
fun NavGraphBuilder.mainNavGraph(
    currentScreen: WindowSizeClass,
    topBarPadding: Dp,
    bottomBarPadding: Dp
) {
    composable<MainRoutes.Home> {
        HomeUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
            topBarPadding = topBarPadding,
            bottomBarPadding = bottomBarPadding,
        )
    }
    composable<MainRoutes.Qibla> {
        QiblaUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
        )
    }
    composable<MainRoutes.Settings> {
        SettingsUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
            topBarPadding = topBarPadding,
//            bottomBarPadding = bottomBarPadding,
        )
    }
}
