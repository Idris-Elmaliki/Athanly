package com.example.athanly.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val preferencesDataStore: AppDataPreferencesDataStore
) : ViewModel() {

    // null while the DataStore read is in flight — AppRoute waits for this to resolve
    // before rendering its NavHost, so it never briefly flashes the wrong start destination.
    private val _hasCompletedOnboarding = MutableStateFlow<Boolean?>(null)
    val hasCompletedOnboarding: StateFlow<Boolean?> = _hasCompletedOnboarding.asStateFlow()

    init {
        // No Dispatchers.IO override here — combine()/collect() on SettingsViewModel and
        // OnBoardingViewModel follow the same pattern. DataStore's own Flow already does
        // its file I/O off the main thread internally, so this just needs viewModelScope's
        // default dispatcher (also keeps this collection synchronized with test dispatchers).
        viewModelScope.launch {
            try {
                preferencesDataStore.observedHasCompletedOnboarding().collect { hasCompleted ->
                    _hasCompletedOnboarding.update { hasCompleted }
                }
            } catch (e: Exception) {
                // Falls back to showing onboarding rather than leaving AppRoute stuck loading forever
                _hasCompletedOnboarding.update { false }
            }
        }
    }
}