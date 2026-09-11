package com.example.athanly.ui.home.viewModel.subScreenVMs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athanly.appData.domain.modelData.Muadhdhin
import com.example.athanly.appData.domain.modelData.MuadhdhinPickerUiState
import com.example.athanly.appData.domain.repository.AthanPreviewPlayer
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Scoped to the MuadhdhinPickerUi screen only — deferred-save selection and preview playback
// don't belong on SettingsViewModel, since none of its other rows (Language, Hanafi, volume,
// time format) need either.
@HiltViewModel
class MuadhhinViewModel @Inject constructor(
    private val preferencesDataStore: AppDataPreferencesDataStore,
    private val athanPreviewPlayer: AthanPreviewPlayer
) : ViewModel() {
    private val _pickerState = MutableStateFlow(MuadhdhinPickerUiState())
    val pickerState: StateFlow<MuadhdhinPickerUiState> = _pickerState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                preferencesDataStore.observedSelectedMuadhdhin().collect { selectedMuadhdhin ->
                    // pendingMuadhdhin is ephemeral UI state, not backed by DataStore —
                    // preserved here so this emission doesn't wipe out an uncommitted pick.
                    // Once onMuadhdhinPickerBackClick's write lands, selectedMuadhdhin catches
                    // up to match pending, and this clears pending back to null on its own.
                    _pickerState.update { current ->
                        val pending = current.pendingMuadhdhin?.takeIf { it != selectedMuadhdhin }
                        current.copy(
                            selectedMuadhdhin = pending ?: selectedMuadhdhin,
                            pendingMuadhdhin = pending
                        )
                    }
                }
            } catch (e: Exception) {
                _pickerState.update { data ->
                    data.copy(error = e.message)
                }
            }
        }
    }

    // Selecting a Muadhdhin also previews it — tapping the one already playing stops it instead
    // of restarting it. MediaPlayer calls are made directly here (on the caller's thread, which
    // is Compose's Main thread for an onClick) rather than inside a launch, since MediaPlayer
    // expects to be driven from a single consistent thread. Nothing is saved to DataStore here —
    // see onMuadhdhinPickerBackClick, which commits pendingMuadhdhin once the screen is left.
    fun onMuadhdhinSelected(muadhdhin: Muadhdhin) {
        if (_pickerState.value.previewingMuadhdhinId == muadhdhin.id) {
            stopPreview()
            return
        }

        athanPreviewPlayer.play(
            audioRes = muadhdhin.otherAudioRes,
            onCompletion = {
                // Only clear if this clip is still the one "playing" — guards against a
                // stale completion callback clearing a newer selection's playing state
                _pickerState.update { current ->
                    if (current.previewingMuadhdhinId == muadhdhin.id) {
                        current.copy(previewingMuadhdhinId = null)
                    } else {
                        current
                    }
                }
            }
        )
        _pickerState.update { current ->
            current.copy(
                previewingMuadhdhinId = muadhdhin.id,
                selectedMuadhdhin = muadhdhin,
                pendingMuadhdhin = muadhdhin
            )
        }
    }

    // Called when the user leaves the Muadhdhin picker screen — commits whichever pick is
    // still pending to DataStore. A no-op if the user never tapped a different Muadhdhin.
    fun onMuadhdhinPickerBackClick() {
        val pendingMuadhdhin = _pickerState.value.pendingMuadhdhin ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferencesDataStore.setSelectedMuadhdhin(pendingMuadhdhin)
            } catch (e: Exception) {
                _pickerState.update { data ->
                    data.copy(error = e.message)
                }
            }
        }
    }

    // Called when the picker screen is left, so a preview doesn't keep playing in the background
    fun stopPreview() {
        athanPreviewPlayer.stop()
        _pickerState.update { current ->
            current.copy(previewingMuadhdhinId = null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        athanPreviewPlayer.stop()
    }
}