package com.example.athanly.athanNotifications.domain.modelData

// Holds the result of the runtime notification permission request on the final onboarding step
data class NotificationsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val isPermissionGranted: Boolean = false,
)