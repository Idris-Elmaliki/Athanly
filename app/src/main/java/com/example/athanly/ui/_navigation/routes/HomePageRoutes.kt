package com.example.athanly.ui._navigation.routes

import kotlinx.serialization.Serializable

// Sealed interface so the compiler enforces exhaustive coverage of all bottom nav tabs
@Serializable
sealed interface HomePageRoutes {

    // @Serializable on each entry so Navigation Compose can serialize them onto the back stack
    @Serializable
    data object Home : HomePageRoutes

    @Serializable
    data object Qibla : HomePageRoutes

    @Serializable
    data object Settings : HomePageRoutes

    // Pushed on top of Settings, not a bottom nav tab — no BottomNavItem entry for either
    @Serializable
    data object LanguagePicker : HomePageRoutes

    @Serializable
    data object MuadhdhinPicker : HomePageRoutes

    // Pushed from Home's top bar, not a bottom nav tab — same as LanguagePicker/MuadhdhinPicker
    @Serializable
    data object ChangeCity : HomePageRoutes
}