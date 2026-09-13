package com.example.athanly.ui.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athanly.qibla.local.data.QiblaPreferencesDataStore
import com.example.athanly.qibla.local.domain.modelData.QiblaUiState
import com.example.athanly.qibla.local.domain.modelData.enums.DistanceUnit
import com.example.athanly.qibla.local.domain.repository.CompassRepository
import com.example.athanly.qibla.local.domain.repository.UserLocationRepository
import com.example.athanly.qibla.remote.domain.repository.QiblaApiRepository
import com.example.athanly.qibla.userInfoCalculations.QiblaCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val userLocationRepository: UserLocationRepository,
    private val compassRepository: CompassRepository,
    private val preferencesDataStore: QiblaPreferencesDataStore,
    private val qiblaApiRepository: QiblaApiRepository
) : ViewModel() {
    private val _qiblaState = MutableStateFlow(QiblaUiState())
    val qiblaState: StateFlow<QiblaUiState> = _qiblaState.asStateFlow()

    init {
        // Needle: a live compass hand driven purely by device heading — updates
        // continuously and has no relationship to the Kaaba's direction. Negated so
        // the needle visually stays pointed at a fixed real-world reference as the
        // phone turns, the way a physical compass needle behaves.
        viewModelScope.launch {
            try {
                compassRepository.observedHeadingDegrees().collect { headingDegrees ->
                    _qiblaState.update { it.copy(needleRotationDegrees = -headingDegrees) }
                }
            } catch (e: Exception) {
                _qiblaState.update { it.copy(error = e.message) }
            }
        }

        // Distance card's value/unit: reactive to location and the unit preference —
        // never to heading, which has nothing to do with distance and would just mean
        // recomputing this pointlessly on every gyroscope tick.
        viewModelScope.launch {
            try {
                combine(
                    userLocationRepository.observedLocation(),
                    preferencesDataStore.observedDistanceUnit()
                ) { coordinates, distanceUnit ->
                    val distanceKm = QiblaCalculator.distanceToKaaba(from = coordinates)
                    val distanceValue = if (distanceUnit == DistanceUnit.MILES) {
                        QiblaCalculator.kmToMiles(km = distanceKm)
                    } else {
                        distanceKm
                    }
                    distanceValue to distanceUnit
                }.collect { (distanceValue, distanceUnit) ->
                    _qiblaState.update {
                        it.copy(isLoading = false, distanceValue = distanceValue, distanceUnit = distanceUnit)
                    }
                }
            } catch (e: Exception) {
                _qiblaState.update { it.copy(isLoading = false, error = e.message) }
            }
        }

        // Kaaba marker/bearing text: a one-shot calculation, not a live subscription.
        // Per design this must only change via an explicit recalculation — never
        // automatically in response to heading (or even ongoing location) changes.
        recalculateQiblaDirection()
    }

    // Recomputes the Kaaba's direction from the current location and updates
    // qiblaDegrees/qiblaDirection, which in turn move the compass's Kaaba marker and
    // the distance card's bearing text. Called once at startup; a future refresh
    // trigger (UI/UX not decided yet) will call this again to move the marker.
    fun recalculateQiblaDirection() {
        viewModelScope.launch {
            try {
                // .first() takes a single snapshot from the location flow rather than
                // subscribing to it — exactly what "only updates via explicit
                // recalculation" requires, instead of combine()'s continuous reacting.
                val coordinates = userLocationRepository.observedLocation().first()
                val bearingDegrees = qiblaApiRepository.fetchQiblaBearing(
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                )

                _qiblaState.update { data ->
                    data.copy(
                        qiblaDegrees = bearingDegrees,
                        qiblaDirection = QiblaCalculator.cardinalDirectionFor(bearingDegrees = bearingDegrees)
                    )
                }
            } catch (e: Exception) {
                _qiblaState.update { data ->
                    data.copy(error = e.message)
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