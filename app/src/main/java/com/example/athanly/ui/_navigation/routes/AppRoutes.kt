package com.example.athanly.ui._navigation.routes

import kotlinx.serialization.Serializable

// Sealed interface so the compiler enforces exhaustive when-branches across all routes
@Serializable
sealed interface AppRoutes {

    // @Serializable on each entry so Navigation 3 can serialize them onto the back stack
    @Serializable
    data object OnBoarding : AppRoutes

    @Serializable
    data object Home : AppRoutes
}