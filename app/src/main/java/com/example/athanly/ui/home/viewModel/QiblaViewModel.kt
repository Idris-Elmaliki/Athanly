package com.example.athanly.ui.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.connectivity.domain.repository.ConnectivityProvider
import com.example.athanly.qibla.local.data.QiblaPreferencesDataStore
import com.example.athanly.qibla.local.domain.modelData.QiblaUiState
import com.example.athanly.qibla.local.domain.modelData.enums.DistanceUnit
import com.example.athanly.qibla.local.domain.repository.CompassRepository
import com.example.athanly.qibla.local.domain.repository.UserLocationRepository
import com.example.athanly.qibla.remote.domain.repository.QiblaApiRepository
import com.example.athanly.userInfoCalculations.QiblaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.plugins.logging.Logging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val userLocationRepository: UserLocationRepository,
    private val compassRepository: CompassRepository,
    private val preferencesDataStore: QiblaPreferencesDataStore,
    private val qiblaApiRepository: QiblaApiRepository,
    private val connectivityProvider: ConnectivityProvider,
    private val appDataPreferencesDataStore: AppDataPreferencesDataStore
) : ViewModel() {
    private val _qiblaState = MutableStateFlow(QiblaUiState())
    val qiblaState: StateFlow<QiblaUiState> = _qiblaState.asStateFlow()

    init {
        // Checked first, synchronously — see checkLocationAccessPreconditions()'s own
        // comment for why this can't just be folded into the coroutines below.
        checkLocationAccessPreconditions()

        // Needle: a live compass hand driven purely by device heading — updates
        // continuously and has no relationship to the Kaaba's direction. Matches the
        // heading directly (no negation) so it lines up with the device's native
        // compass: e.g. facing due east should rotate the needle toward the fixed "E"
        // label, not "W" — negating here previously mirrored east and west.
        viewModelScope.launch {
            try {
                compassRepository.currentPhoneCompassHeadingDegrees().collect { headingDegrees ->
                    _qiblaState.update { data ->
                        data.copy(
                            needleRotationDegrees = headingDegrees
                        ) 
                    }
                }
            } catch (e: Exception) {
                _qiblaState.update { data ->
                    data.copy(
                        error = e.message
                    ) 
                }
            }
        }

        observeDistanceUnit()

        // Distance and Kaaba bearing both come from this one-shot fetch — never from
        // heading or ongoing location changes.
        refresh()
    }

    // One fetch feeds both the distance and the bearing — the user is stationary, so there's
    // nothing to subscribe to and no Job to track.
    private fun refresh() {
        viewModelScope.launch {
            try {
                val coordinates = userLocationRepository.getCurrentLocation()

                Log.d("Qibla coordinates", "Gathering user coordinates: $coordinates")
                val distanceKm = QiblaCalculator.distanceToKaaba(from = coordinates)
                val bearingDegrees = qiblaApiRepository.fetchQiblaBearing(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                )
                Log.d("Qibla coordinates", "Gathering Qibla coordinates: $bearingDegrees")

                _qiblaState.update { data ->
                    data.copy(
                        isLoading = false,
                        distanceKm = distanceKm,
                        // Converted with the unit current at write time, so a toggle that
                        // landed before the fetch finished isn't overwritten.
                        distanceValue = 
                            if (data.distanceUnit == DistanceUnit.MILES) {
                                QiblaCalculator.kmToMiles(km = distanceKm)
                            } else {
                                distanceKm
                            },
                        qiblaDegrees = bearingDegrees,
                        qiblaDirection = QiblaCalculator.cardinalDirectionFor(bearingDegrees = bearingDegrees),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _qiblaState.update { data ->
                    data.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    // The unit is the one input that can still change after the fetch (the km/mi toggle), so
    // it's the only thing observed — it re-converts the stored km instead of refetching.
    private fun observeDistanceUnit() {
        viewModelScope.launch {
            try {
                preferencesDataStore.observedDistanceUnit().collect { distanceUnit ->
                    _qiblaState.update { data ->
                        data.copy(
                            distanceUnit = distanceUnit,
                            distanceValue =
                                if (distanceUnit == DistanceUnit.MILES) {
                                    QiblaCalculator.kmToMiles(km = data.distanceKm)
                                } else {
                                    data.distanceKm
                                }
                        )
                    }
                }
            } catch (e: Exception) {
                _qiblaState.update { data ->
                    data.copy(error = e.message)
                }
            }
        }
    }

    // Called after the user returns from the system location settings screen (see the
    // location-off blocker) — re-runs the one-shot refresh for distance and bearing, since
    // the first attempt gave up after its failure. Also rechecks
    // connectivity/permission first, since that's exactly the round trip (leave app → fix
    // the setting → come back) this is meant to catch.
    fun retryLocationAccess() {
        checkLocationAccessPreconditions()

        // Back to "fetching" with the old failure cleared, so the UI doesn't keep showing
        // a stale error (or no loading state) while the fresh attempt is in flight.
        _qiblaState.update { data ->
            data.copy(
                isLoading = true,
                error = null
            )
        }

        refresh()
    }

    // isOnline()/hasLocationPermission() are plain synchronous checks, not suspend calls —
    // updating state from them directly (no launch{}) keeps this deterministic and ordered
    // ahead of the other init{} coroutines, rather than racing them on a separate dispatcher.
    // Only the DataStore write below is genuine I/O, so only that part needs Dispatchers.IO.
    private fun checkLocationAccessPreconditions() {
        val isOnline = connectivityProvider.isOnline()
        val isLocationPermissionGranted = userLocationRepository.hasLocationPermission()
        // Separate from permission: the device toggle can be off even when the app is allowed.
        val isLocationServicesEnabled = userLocationRepository.isLocationEnabled()

        _qiblaState.update { data ->
            data.copy(
                isOnline = isOnline,
                isLocationPermissionGranted = isLocationPermissionGranted,
                isLocationServicesEnabled = isLocationServicesEnabled
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Written even though this ViewModel never reads it back — this keeps
                // AppDataPreferencesDataStore's flag from going stale if permission is
                // revoked in system Settings after onboarding already wrote it once.
                appDataPreferencesDataStore.setIsLocationPermissionGranted(isLocationPermissionGranted)
            } catch (e: Exception) {
                _qiblaState.update { data ->
                    data.copy(
                        error = e.message
                    ) 
                }
            }
        }
    }

    fun onDistanceUnitToggled() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nextUnit =
                    if (_qiblaState.value.distanceUnit == DistanceUnit.KILOMETERS) {
                        DistanceUnit.MILES
                    }
                    else {
                        DistanceUnit.KILOMETERS
                    }

                preferencesDataStore.setDistanceUnit(nextUnit)
            } catch (e: Exception) {
                _qiblaState.update { data ->
                    data.copy(
                        error = e.message
                    )
                }
            }
        }
    }
}