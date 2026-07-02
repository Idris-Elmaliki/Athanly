package com.example.athanly.calculations.domain.modelData

// Holds the toggle state for the Hanafi madhab Asr calculation mode
data class CalculationsUiState(
    val isLoading : Boolean = false, 
    val error : String? = null, 
    
    val isHanafiMode: Boolean = false,
)