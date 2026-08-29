package com.example.athanly.ui.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athanly.appData.domain.modelData.Muadhdhin
import com.example.athanly.appData.domain.modelData.Language
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.appData.domain.modelData.AppDataUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesDataStore: AppDataPreferencesDataStore
) : ViewModel() {
    private val _settingsState = MutableStateFlow(AppDataUiState())
    val settingsState: StateFlow<AppDataUiState> = _settingsState.asStateFlow()

    init {
        // combine()'s 5-flow overload — every preference is independent, so any single
        // one changing (e.g. just toggling Hanafi mode) re-emits the full state with
        // the other four values carried over unchanged.
        viewModelScope.launch {
            try {
                combine(
                    preferencesDataStore.observedIs24HourFormat(),
                    preferencesDataStore.observedIsHanafiMode(),
                    preferencesDataStore.observedAthanVolume(),
                    preferencesDataStore.observedLanguage(),
                    preferencesDataStore.observedSelectedMuadhdhin(),
                ) { is24HourFormat, isHanafiMode, athanVolume, language, selectedMuadhdhin ->
                    AppDataUiState(
                        is24HourFormat = is24HourFormat,
                        isHanafiMode = isHanafiMode,
                        athanVolume = athanVolume,
                        selectedLanguage = language,
                        selectedMuadhdhin = selectedMuadhdhin,
                    )
                }.collect { combinedState ->
                    _settingsState.update { combinedState }
                }
            } catch (e: Exception) {
                _settingsState.update { data ->
                    data.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    // Reads the current value straight off settingsState rather than taking a Boolean
    // parameter — the Switch in SettingsRow only ever needs to flip whatever is on screen.
    fun onTimeFormatToggled() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferencesDataStore.setIs24HourFormat(!_settingsState.value.is24HourFormat)
            } catch (e: Exception) {
                _settingsState.update { data ->
                    data.copy(
                        error = e.message
                    )
                }
            }
        }
    }

    fun onHanafiModeToggled() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferencesDataStore.setIsHanafiMode(!_settingsState.value.isHanafiMode)
            } catch (e: Exception) {
                _settingsState.update { data ->
                    data.copy(
                        error = e.message
                    )
                }
            }
        }
    }

    fun onAthanVolumeChanged(volume: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferencesDataStore.setAthanVolume(volume)
            } catch (e: Exception) {
                _settingsState.update { data ->
                    data.copy(
                        error = e.message
                    )
                }
            }
        }
    }

    fun onLanguageSelected(language: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferencesDataStore.setLanguage(language)
            } catch (e: Exception) {
                _settingsState.update { data ->
                    data.copy(
                        error = e.message
                    )
                }
            }
        }
    }

    fun onMuadhdhinSelected(muadhdhin: Muadhdhin) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferencesDataStore.setSelectedMuadhdhin(muadhdhin)
            } catch (e: Exception) {
                _settingsState.update { data ->
                    data.copy(
                        error = e.message
                    )
                }
            }
        }
    }
}