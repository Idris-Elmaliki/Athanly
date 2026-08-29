package com.example.athanly.appData.domain.modelData

// Holds every control shown on the Settings screen, backed by AppDataPreferencesDataStore
data class AppDataUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val selectedLanguage: Language = Language.English,
    val is24HourFormat: Boolean = false,
    val isHanafiMode: Boolean = false,
    val selectedMuadhdhin: Muadhdhin = muadhhinList.first(),

    // 0f-1f range, drives the Athan playback volume slider
    val athanVolume: Float = 1f,
)