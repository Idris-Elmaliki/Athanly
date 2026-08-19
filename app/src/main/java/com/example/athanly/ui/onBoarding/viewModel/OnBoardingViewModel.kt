package com.example.athanly.ui.onBoarding.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athanly.athanSelection.domain.modelData.AthanSelectionUiState
import com.example.athanly.athanSelection.domain.modelData.Muadhdhin
import com.example.athanly.calculations.domain.modelData.CalculationsUiState
import com.example.athanly.language.domain.modelData.Language
import com.example.athanly.language.domain.modelData.WelcomeUiState
import com.example.athanly.notifications.domain.modelData.NotificationsUiState
import com.example.athanly.settings.data.SettingsPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val preferencesDataStore: SettingsPreferencesDataStore
) : ViewModel() {

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

    // Called whenever the user flips the Hanafi mode toggle. Updates in-memory state
    // immediately for the UI, and separately writes through to SettingsPreferencesDataStore
    // so the choice survives past onboarding instead of being lost once this ViewModel dies.
    fun onHanafiModeToggled() {
        _calculationsState.update { current ->
            current.copy(isHanafiMode = !current.isHanafiMode)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferencesDataStore.setIsHanafiMode(_calculationsState.value.isHanafiMode)
            } catch (e: Exception) {
                _calculationsState.update { it.copy(error = e.message) }
            }
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