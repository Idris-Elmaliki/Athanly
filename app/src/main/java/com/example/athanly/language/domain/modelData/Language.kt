package com.example.athanly.language.domain.modelData

// Represents the two supported app languages collected during onboarding
sealed class Language {
    data object English : Language()
    data object Arabic : Language()
}