package com.example.athanly.ui._navigation.routes

import kotlinx.serialization.Serializable

// Sealed interface so the compiler enforces exhaustive coverage of all bottom nav tabs
@Serializable
sealed interface MainRoutes {

    // @Serializable on each entry so Navigation Compose can serialize them onto the back stack
    @Serializable
    data object Home : MainRoutes

    @Serializable
    data object Qibla : MainRoutes

    @Serializable
    data object Settings : MainRoutes
}