package com.example.athanly.ui.qibla.viewModel

import app.cash.turbine.test
import com.example.athanly.qibla.data.QiblaPreferencesDataStore
import com.example.athanly.qibla.domain.local.modelData.Coordinates
import com.example.athanly.qibla.domain.local.modelData.enums.DistanceUnit
import com.example.athanly.qibla.domain.local.repository.CompassRepository
import com.example.athanly.qibla.domain.local.repository.UserLocationRepository
import com.example.athanly.qibla.userInfoCalculations.QiblaCalculator
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class QiblaViewModelTest {

    // Same reasoning as HomeViewModelTest: viewModelScope needs Dispatchers.Main
    // substituted before construction, and Unconfined so the init{} coroutine's
    // combine() runs eagerly instead of needing manual time-advancement.
    private val mainDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(mainDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `qiblaState derives needle rotation from heading alone and distance-bearing from location and unit`() = runTest {
        val coordinates = Coordinates(latitude = 40.7128, longitude = -74.0060)
        val headingDegrees = 10f

        // mockk<T> { every { ... } returns ... } builds a fake implementation of the
        // interface/class where every method call returns a hardcoded value we control,
        // instead of running real GPS/sensor/DataStore code.
        val userLocationRepository = mockk<UserLocationRepository> {
            every { 
                observedLocation() 
            } returns flowOf(coordinates)
        }
        
        val compassRepository = mockk<CompassRepository> {
            every { 
                observedHeadingDegrees() 
            } returns flowOf(headingDegrees)
        }
        
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { 
                observedDistanceUnit() 
            } returns flowOf(DistanceUnit.KILOMETERS)
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore
        )

        viewModel.qiblaState.test {
            val state = awaitItem()

            // Expected values are computed via QiblaCalculator itself (already covered
            // by its own tests) — this test is only checking that QiblaViewModel wires
            // its inputs into the right state fields, not re-verifying the formula's math.
            val expectedBearing = QiblaCalculator.bearingToKaaba(from = coordinates)
            val expectedDistanceKm = QiblaCalculator.distanceToKaaba(from = coordinates)

            // Needle is now purely -heading — no bearing involved at all, since it's
            // decoupled from the Kaaba's direction (the coupling that used to exist here
            // — bearing minus heading — was the bug this rearchitecture fixes).
            assertEquals(-headingDegrees, state.needleRotationDegrees, 0.01f)
            assertEquals(expectedDistanceKm, state.distanceValue, 0.01)
            assertEquals(DistanceUnit.KILOMETERS, state.distanceUnit)
            assertEquals(expectedBearing, state.qiblaDegrees, 0.01)
            assertEquals(QiblaCalculator.cardinalDirectionFor(bearingDegrees = expectedBearing), state.qiblaDirection)
        }
    }

    @Test
    fun `needleRotationDegrees updates on new heading emissions while qiblaDegrees stays untouched`() = runTest {
        val coordinates = Coordinates(latitude = 40.7128, longitude = -74.0060)

        // MutableStateFlow (vs flowOf's fixed single value) lets this test push a
        // second heading value after construction, simulating a live sensor update —
        // exactly the scenario the old coupled implementation got wrong.
        val headingFlow = MutableStateFlow(10f)

        val userLocationRepository = mockk<UserLocationRepository> {
            every { observedLocation() } returns flowOf(coordinates)
        }
        val compassRepository = mockk<CompassRepository> {
            every { observedHeadingDegrees() } returns headingFlow
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore
        )

        viewModel.qiblaState.test {
            val initialState = awaitItem()
            val qiblaDegreesBeforeHeadingChange = initialState.qiblaDegrees

            headingFlow.value = 45f

            // The needle's collect{} re-runs on this new emission, producing a new
            // QiblaUiState (different needleRotationDegrees), which StateFlow emits
            // as the next distinct item.
            val stateAfterHeadingChange = awaitItem()

            assertEquals(-45f, stateAfterHeadingChange.needleRotationDegrees, 0.01f)
            // The whole point of the fix: a heading change must not touch qiblaDegrees.
            assertEquals(qiblaDegreesBeforeHeadingChange, stateAfterHeadingChange.qiblaDegrees, 0.01)
        }
    }

    @Test
    fun `qiblaDegrees only updates when recalculateQiblaDirection is called, not when location drifts on its own`() = runTest {
        val coordinatesA = Coordinates(latitude = 40.7128, longitude = -74.0060)
        val coordinatesB = Coordinates(latitude = 51.5074, longitude = -0.1278)

        // MutableStateFlow again — this simulates location changing in the background
        // (e.g. the user walking) without any explicit recalculation being requested.
        val locationFlow = MutableStateFlow(coordinatesA)

        val userLocationRepository = mockk<UserLocationRepository> {
            every { observedLocation() } returns locationFlow
        }
        val compassRepository = mockk<CompassRepository> {
            every { observedHeadingDegrees() } returns flowOf(0f)
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore
        )

        viewModel.qiblaState.test {
            val initialState = awaitItem()
            val expectedBearingA = QiblaCalculator.bearingToKaaba(from = coordinatesA)
            assertEquals(expectedBearingA, initialState.qiblaDegrees, 0.01)

            locationFlow.value = coordinatesB

            // Distance is still a live subscriber to location, so it reacts immediately
            // — this is the contrast that proves qiblaDegrees's staleness is deliberate,
            // not an accident of the mocks used.
            val stateAfterLocationDrift = awaitItem()
            val expectedDistanceB = QiblaCalculator.distanceToKaaba(from = coordinatesB)
            assertEquals(expectedDistanceB, stateAfterLocationDrift.distanceValue, 0.01)
            // qiblaDegrees is untouched by the location drift alone.
            assertEquals(expectedBearingA, stateAfterLocationDrift.qiblaDegrees, 0.01)

            viewModel.recalculateQiblaDirection()

            // Only now, after the explicit call, does qiblaDegrees catch up to the
            // Kaaba bearing from the phone's current (drifted) location.
            val stateAfterRecalculation = awaitItem()
            val expectedBearingB = QiblaCalculator.bearingToKaaba(from = coordinatesB)
            assertEquals(expectedBearingB, stateAfterRecalculation.qiblaDegrees, 0.01)
        }
    }
}