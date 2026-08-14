package com.example.athanly.settings.domain.local.modelData

import com.example.athanly.athanSelection.domain.modelData.Muadhdhin
import com.example.athanly.athanSelection.domain.modelData.muadhhinList
import com.example.athanly.language.domain.modelData.Language

// Holds every control shown on the Settings screen, backed by SettingsPreferencesDataStore
data class SettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val selectedLanguage: Language = Language.English,
    val is24HourFormat: Boolean = false,
    val isHanafiMode: Boolean = false,
    val selectedMuadhdhin: Muadhdhin = muadhhinList.first(),

    // 0f-1f range, drives the Athan playback volume slider
    val athanVolume: Float = 1f,
)