package com.example.athanly.location.domain.modelData

// Holds the result of the runtime location permission request + the current-location fetch
// on the onboarding Location screen — mirrors NotificationsUiState's shape.
data class LocationUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val isPermissionGranted: Boolean = false,
    // True once a city (GPS fix or preset pick) has actually been saved — lets the UI
    // know it's safe to advance past this onboarding step.
    val hasSavedCity: Boolean = false,
)