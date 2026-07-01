package com.example.athanly.athanSelection.domain.modelData

data class AthanSelectionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Defaults to Makkah to match the Figma active state
    val selectedMuadhdhin: Muadhdhin = muadhhinList.first(),
)
