package com.example.athanly.language.domain.modelData

import androidx.annotation.StringRes
import com.example.athanly.R

// Represents the two supported app languages collected during onboarding.
// displayNameRes lets any screen (e.g. Settings) show the current selection
// without duplicating a when-branch over Language elsewhere.
sealed class Language(@StringRes val displayNameRes: Int) {
    data object English : Language(displayNameRes = R.string.welcome_language_english)
    data object Arabic : Language(displayNameRes = R.string.welcome_language_arabic)
}