package com.example.athanly.ui.home.viewModel

import android.media.AudioManager
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
import kotlin.math.roundToInt

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesDataStore: AppDataPreferencesDataStore,
    private val audioManager: AudioManager
) : ViewModel() {
    private val _settingsState = MutableStateFlow(AppDataUiState())
    val settingsState: StateFlow<AppDataUiState> = _settingsState.asStateFlow()

    init {
        // The slider reflects/controls the phone's actual STREAM_NOTIFICATION volume rather
        // than a custom DataStore value, so it's read once here rather than as a Flow —
        // AudioManager has no built-in way to observe it, only the hardware volume rocker
        // (while this screen is open) can change it out from under this read.
        _settingsState.update { current ->
            current.copy(athanVolume = currentNotificationVolumeFraction())
        }

        // combine()'s 4-flow overload — every preference is independent, so any single
        // one changing (e.g. just toggling Hanafi mode) re-emits the full state with
        // the other three values carried over unchanged.
        viewModelScope.launch {
            try {
                combine(
                    preferencesDataStore.observedIs24HourFormat(),
                    preferencesDataStore.observedIsHanafiMode(),
                    preferencesDataStore.observedLanguage(),
                    preferencesDataStore.observedSelectedMuadhdhin(),
                ) { is24HourFormat, isHanafiMode, language, selectedMuadhdhin ->
                    AppDataUiState(
                        is24HourFormat = is24HourFormat,
                        isHanafiMode = isHanafiMode,
                        selectedLanguage = language,
                        selectedMuadhdhin = selectedMuadhdhin,
                    )
                }.collect { combinedState ->
                    // athanVolume isn't backed by DataStore anymore — preserved here so this
                    // combine (triggered by an unrelated preference) doesn't reset the slider
                    // back to AppDataUiState's default
                    _settingsState.update { current ->
                        combinedState.copy(athanVolume = current.athanVolume)
                    }
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

    private fun currentNotificationVolumeFraction(): Float {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
        if (maxVolume == 0) return 0f

        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        return currentVolume.toFloat() / maxVolume
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

    // Sets the phone's actual notification volume directly — no DataStore write, no IO
    // dispatch needed, since AudioManager calls are cheap, synchronous, and local to the device.
    fun onAthanVolumeChanged(volume: Float) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
        audioManager.setStreamVolume(
            AudioManager.STREAM_NOTIFICATION,
            (volume * maxVolume).roundToInt(),
            0
        )
        _settingsState.update { current ->
            current.copy(athanVolume = volume)
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