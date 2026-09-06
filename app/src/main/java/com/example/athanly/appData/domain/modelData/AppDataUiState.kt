package com.example.athanly.appData.domain.modelData

// Holds every control shown on the Settings screen, backed by AppDataPreferencesDataStore
data class AppDataUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val selectedLanguage: Language = Language.English,
    val is24HourFormat: Boolean = false,
    val isHanafiMode: Boolean = false,
    val selectedMuadhdhin: Muadhdhin = muadhhinList.first(),

    // DataStore's own read always falls back to muadhhinList.first() when nothing has been
    // chosen yet, so this flag (not selectedMuadhdhin itself) is what tells onboarding's Athan
    // Selection step apart from "the user already picked the first one" — same shape as
    // LocationUiState.hasSavedCity. Settings never sets this (a real choice always exists by
    // the time onboarding is done), so it stays false and unused there.
    val hasSelectedMuadhdhin: Boolean = false,

    // 0f-1f range, drives the Athan playback volume slider
    val athanVolume: Float = 1f,
)