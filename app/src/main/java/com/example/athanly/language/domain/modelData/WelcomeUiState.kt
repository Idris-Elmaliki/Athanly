package com.example.athanly.language.domain.modelData

data class WelcomeUiState(
    // Which language the user has tapped — defaults to English to match the Figma active state
    val isLoading : Boolean = false,
    val error : String? = null,

    val selectedLanguage: Language = Language.English
)