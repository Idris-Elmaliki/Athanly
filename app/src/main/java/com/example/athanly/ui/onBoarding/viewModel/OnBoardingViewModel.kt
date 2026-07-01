package com.example.athanly.ui.onBoarding.viewModel

import androidx.lifecycle.ViewModel
import com.example.athanly.athanSelection.domain.modelData.AthanSelectionUiState
import com.example.athanly.athanSelection.domain.modelData.Muadhdhin
import com.example.athanly.language.domain.modelData.Language
import com.example.athanly.language.domain.modelData.WelcomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor() : ViewModel() {

    private val _welcomeState = MutableStateFlow(WelcomeUiState())
    val welcomeState: StateFlow<WelcomeUiState> = _welcomeState.asStateFlow()

    // Called whenever the user taps a language card
    fun onLanguageSelected(language: Language) {
        _welcomeState.update { current ->
            current.copy(selectedLanguage = language)
        }
    }

    private val _athanSelectionState = MutableStateFlow(AthanSelectionUiState())
    val athanSelectionState: StateFlow<AthanSelectionUiState> = _athanSelectionState.asStateFlow()

    // Called whenever the user taps a Muadhdhin card
    fun onMuadhhinSelected(muadhdhin: Muadhdhin) {
        _athanSelectionState.update { current ->
            current.copy(selectedMuadhdhin = muadhdhin)
        }
    }
}