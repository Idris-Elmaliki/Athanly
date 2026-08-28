package com.example.athanly.ui._navigation.navGraphs

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui._navigation.routes.MainRoutes
import com.example.athanly.ui.home.screens.home.HomeUi
import com.example.athanly.ui.home.screens.qibla.QiblaUi
import com.example.athanly.ui.home.screens.settings.SettingsUi
import com.example.athanly.ui.home.screens.settings.settingsSubScreens.languagePicker.LanguagePickerUi
import com.example.athanly.ui.home.screens.settings.settingsSubScreens.muadhdhinPicker.MuadhdhinPickerUi

// Shared by every Settings -> picker push transition here, and by HomeLayout's BottomNavBar
// visibility animation, so the bar hides in lockstep with the screen sliding in instead of
// snapping away the instant navigate() is called.
const val SettingsDetailTransitionDurationMillis = 300

// Extension on NavGraphBuilder so MainLayout stays clean — each feature owns its own graph
fun NavGraphBuilder.mainNavGraph(
    currentScreen: WindowSizeClass,
    topBarPadding: Dp,
    bottomBarPadding: Dp,
    navController: NavController
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
            onLanguageRowClick = { navController.navigate(MainRoutes.LanguagePicker) },
            onMuadhdhinRowClick = { navController.navigate(MainRoutes.MuadhdhinPicker) },
//            bottomBarPadding = bottomBarPadding,
        )
    }
    // Pushed from Settings — slides in from the right on the way in, slides back out to the
    // right when popped, instead of NavHost's default fade (see SettingsDetailTransitionDurationMillis)
    composable<MainRoutes.LanguagePicker>(
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(SettingsDetailTransitionDurationMillis)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(SettingsDetailTransitionDurationMillis)
            )
        }
    ) {
        LanguagePickerUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
            topBarPadding = topBarPadding,
            onBackClick = { navController.popBackStack() }
        )
    }
    composable<MainRoutes.MuadhdhinPicker>(
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(SettingsDetailTransitionDurationMillis)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(SettingsDetailTransitionDurationMillis)
            )
        }
    ) {
        MuadhdhinPickerUi(
            modifier = Modifier.fillMaxSize(),
            windowSizeClass = currentScreen,
            topBarPadding = topBarPadding,
            onBackClick = { navController.popBackStack() }
        )
    }
}