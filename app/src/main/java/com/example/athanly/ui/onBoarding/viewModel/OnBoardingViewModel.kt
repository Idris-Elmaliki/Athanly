package com.example.athanly.ui.onBoarding.viewModel

import androidx.lifecycle.ViewModel
import com.example.athanly.athanSelection.domain.modelData.AthanSelectionUiState
import com.example.athanly.athanSelection.domain.modelData.Muadhdhin
import com.example.athanly.calculations.domain.modelData.CalculationsUiState
import com.example.athanly.language.domain.modelData.Language
import com.example.athanly.language.domain.modelData.WelcomeUiState
import com.example.athanly.notifications.domain.modelData.NotificationsUiState
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

    private val _calculationsState = MutableStateFlow(CalculationsUiState())
    val calculationsState: StateFlow<CalculationsUiState> = _calculationsState.asStateFlow()

    // Called whenever the user flips the Hanafi mode toggle
    fun onHanafiModeToggled() {
        _calculationsState.update { current ->
            current.copy(isHanafiMode = !current.isHanafiMode)
        }
    }

    private val _notificationsState = MutableStateFlow(NotificationsUiState())
    val notificationsState: StateFlow<NotificationsUiState> = _notificationsState.asStateFlow()

    // Called with the OS permission dialog result on the final onboarding step
    fun onNotificationPermissionResult(isGranted: Boolean) {
        _notificationsState.update { current ->
            current.copy(isPermissionGranted = isGranted)
        }
    }
}