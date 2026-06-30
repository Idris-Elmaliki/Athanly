package com.example.athanly.ui.onBoarding.viewModel

import androidx.lifecycle.ViewModel
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

    private val _state = MutableStateFlow(WelcomeUiState())
    val state: StateFlow<WelcomeUiState> = _state.asStateFlow()

    // Called whenever the user taps a language card
    fun onLanguageSelected(language: Language) {
        _state.update { current ->
            current.copy(selectedLanguage = language)
        }
    }
}