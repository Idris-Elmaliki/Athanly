package com.example.athanly.ui._navigation.routes

import kotlinx.serialization.Serializable

// Sealed interface so the compiler enforces exhaustive coverage of all onboarding steps
@Serializable
sealed interface OnBoardingRoutes {

    // @Serializable on each entry so Navigation Compose can serialize them onto the back stack
    @Serializable
    data object Welcome : OnBoardingRoutes

    @Serializable
    data object AthanSelection : OnBoardingRoutes

    @Serializable
    data object Calculations : OnBoardingRoutes

    @Serializable
    data object Notifications : OnBoardingRoutes
}