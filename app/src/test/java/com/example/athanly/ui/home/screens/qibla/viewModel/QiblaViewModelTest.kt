package com.example.athanly.ui.home.screens.qibla.viewModel

import app.cash.turbine.test
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.connectivity.domain.repository.ConnectivityProvider
import com.example.athanly.qibla.local.data.QiblaPreferencesDataStore
import com.example.athanly.qibla.local.domain.modelData.Coordinates
import com.example.athanly.qibla.local.domain.modelData.enums.DistanceUnit
import com.example.athanly.qibla.local.domain.repository.CompassRepository
import com.example.athanly.qibla.local.domain.repository.UserLocationRepository
import com.example.athanly.qibla.remote.domain.repository.QiblaApiRepository
import com.example.athanly.userInfoCalculations.QiblaCalculator
import com.example.athanly.ui.home.viewModel.QiblaViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
            coEvery {
                getCurrentLocation()
            } returns coordinates
            every { hasLocationPermission() } returns true
            every { isLocationEnabled() } returns true
        }

        val compassRepository = mockk<CompassRepository> {
            every {
                currentPhoneCompassHeadingDegrees()
            } returns flowOf(headingDegrees)
        }

        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every {
                observedDistanceUnit()
            } returns flowOf(DistanceUnit.KILOMETERS)
        }

        // Bearing now comes from QiblaApiRepository (the Aladhan API), not a local
        // formula — coEvery stubs its suspend fetchQiblaBearing call with a fixed value.
        val expectedBearing = 136.14
        val qiblaApiRepository = mockk<QiblaApiRepository> {
            coEvery {
                fetchQiblaBearing(latitude = coordinates.latitude, longitude = coordinates.longitude)
            } returns expectedBearing
        }

        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns true
        }

        // `just Runs` stubs a suspend function that returns Unit — there's no return value
        // to hand back, just an "acknowledge this call happened" stub.
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        viewModel.qiblaState.test {
            val state = awaitItem()

            // Distance is still computed via QiblaCalculator locally (already covered by
            // its own tests) — this test is only checking that QiblaViewModel wires its
            // inputs into the right state fields, not re-verifying the formula's math.
            val expectedDistanceKm = QiblaCalculator.distanceToKaaba(from = coordinates)

            // Needle is now purely heading — no bearing involved at all, since it's
            // decoupled from the Kaaba's direction (the coupling that used to exist here
            // — bearing minus heading — was the bug this rearchitecture fixes). Not negated:
            // it must match the device's native compass heading directly, e.g. facing due
            // east rotates the needle toward the fixed "E" label, not "W".
            assertEquals(headingDegrees, state.needleRotationDegrees, 0.01f)
            assertEquals(expectedDistanceKm, state.distanceValue, 0.01)
            assertEquals(DistanceUnit.KILOMETERS, state.distanceUnit)
            assertEquals(expectedBearing, state.qiblaDegrees!!, 0.01)
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
            coEvery { getCurrentLocation() } returns coordinates
            every { hasLocationPermission() } returns true
            every { isLocationEnabled() } returns true
        }
        val compassRepository = mockk<CompassRepository> {
            every { currentPhoneCompassHeadingDegrees() } returns headingFlow
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }
        val qiblaApiRepository = mockk<QiblaApiRepository> {
            coEvery {
                fetchQiblaBearing(latitude = coordinates.latitude, longitude = coordinates.longitude)
            } returns 136.14
        }
        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns true
        }
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        viewModel.qiblaState.test {
            val initialState = awaitItem()
            val qiblaDegreesBeforeHeadingChange = initialState.qiblaDegrees

            headingFlow.value = 45f

            // The needle's collect{} re-runs on this new emission, producing a new
            // QiblaUiState (different needleRotationDegrees), which StateFlow emits
            // as the next distinct item.
            val stateAfterHeadingChange = awaitItem()

            assertEquals(45f, stateAfterHeadingChange.needleRotationDegrees, 0.01f)
            // The whole point of the fix: a heading change must not touch qiblaDegrees.
            assertEquals(qiblaDegreesBeforeHeadingChange!!, stateAfterHeadingChange.qiblaDegrees!!, 0.01)
        }
    }

    @Test
    fun `qiblaState reflects offline and permission-denied from ConnectivityProvider and UserLocationRepository at init`() = runTest {
        val coordinates = Coordinates(latitude = 40.7128, longitude = -74.0060)

        val userLocationRepository = mockk<UserLocationRepository> {
            coEvery { getCurrentLocation() } returns coordinates
            // false here is the thing this test is actually about — everything else
            // below is just enough of a working setup that the rest of init{} doesn't
            // also throw and add noise to the assertion.
            every { hasLocationPermission() } returns false
            every { isLocationEnabled() } returns true
        }
        val compassRepository = mockk<CompassRepository> {
            every { currentPhoneCompassHeadingDegrees() } returns flowOf(0f)
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }
        val qiblaApiRepository = mockk<QiblaApiRepository> {
            coEvery { fetchQiblaBearing(latitude = coordinates.latitude, longitude = coordinates.longitude) } returns 136.14
        }
        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns false
        }
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        viewModel.qiblaState.test {
            val state = awaitItem()
            assertFalse(state.isOnline)
            assertFalse(state.isLocationPermissionGranted)
        }
    }

    @Test
    fun `retryLocationAccess rechecks connectivity and permission, updating qiblaState with the fresh result`() = runTest {
        val coordinates = Coordinates(latitude = 40.7128, longitude = -74.0060)

        val userLocationRepository = mockk<UserLocationRepository> {
            coEvery { getCurrentLocation() } returns coordinates
            every { hasLocationPermission() } returns true
            every { isLocationEnabled() } returns true
        }
        val compassRepository = mockk<CompassRepository> {
            every { currentPhoneCompassHeadingDegrees() } returns flowOf(0f)
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }
        val qiblaApiRepository = mockk<QiblaApiRepository> {
            coEvery { fetchQiblaBearing(latitude = coordinates.latitude, longitude = coordinates.longitude) } returns 136.14
        }
        // Starts online — retryLocationAccess() is what's expected to notice it went offline.
        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns true
        }
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        viewModel.qiblaState.test {
            val initialState = awaitItem()
            assertTrue(initialState.isOnline)

            // Re-stubbing the same mock function overrides its previous answer — simulates
            // the device having gone offline between the initial check and this retry, e.g.
            // after the user backgrounds the app and returns (ON_RESUME → retryLocationAccess()).
            every { connectivityProvider.isOnline() } returns false

            viewModel.retryLocationAccess()

            val stateAfterRetry = awaitItem()
            assertFalse(stateAfterRetry.isOnline)
        }
    }

    @Test
    fun `checkLocationAccessPreconditions persists the permission result to AppDataPreferencesDataStore`() = runTest {
        val coordinates = Coordinates(latitude = 40.7128, longitude = -74.0060)

        val userLocationRepository = mockk<UserLocationRepository> {
            coEvery { getCurrentLocation() } returns coordinates
            every { hasLocationPermission() } returns true
            every { isLocationEnabled() } returns true
        }
        val compassRepository = mockk<CompassRepository> {
            every { currentPhoneCompassHeadingDegrees() } returns flowOf(0f)
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }
        val qiblaApiRepository = mockk<QiblaApiRepository> {
            coEvery { fetchQiblaBearing(latitude = coordinates.latitude, longitude = coordinates.longitude) } returns 136.14
        }
        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns true
        }
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        // The write happens inside a viewModelScope.launch(Dispatchers.IO) block, off the
        // UnconfinedTestDispatcher this test's other coroutines run on — coVerify's timeout
        // parameter polls with real time for up to 1 second instead of asserting instantly,
        // since there's no virtual-time control over the real IO dispatcher.
        coVerify(timeout = 1_000) {
            appDataPreferencesDataStore.setIsLocationPermissionGranted(true)
        }
    }

    // Pattern is the same as the offline/permission test above: stub one repository answer,
    // build the ViewModel, then assert the matching QiblaUiState field.
    @Test
    fun `qiblaState reflects location services being off from UserLocationRepository at init`() = runTest {
        val coordinates = Coordinates(latitude = 40.7128, longitude = -74.0060)

        val userLocationRepository = mockk<UserLocationRepository> {
            coEvery { getCurrentLocation() } returns coordinates
            // Permission granted but the device toggle off — the exact case that used to
            // show no blocker at all. This is the one answer the test is really about.
            every { hasLocationPermission() } returns true
            every { isLocationEnabled() } returns false
        }
        val compassRepository = mockk<CompassRepository> {
            every { currentPhoneCompassHeadingDegrees() } returns flowOf(0f)
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }
        val qiblaApiRepository = mockk<QiblaApiRepository> {
            coEvery { fetchQiblaBearing(latitude = coordinates.latitude, longitude = coordinates.longitude) } returns 136.14
        }
        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns true
        }
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        viewModel.qiblaState.test {
            val state = awaitItem()

            // Permission is fine, so only the services flag should differ from the defaults.
            assertTrue(state.isLocationPermissionGranted)
            assertFalse(state.isLocationServicesEnabled)
        }
    }

    // RED test: retryLocationAccess() doesn't reset isLoading/error yet, so this fails first.
    @Test
    fun `retryLocationAccess resets isLoading to true and clears the previous error while refetching`() = runTest {
        val userLocationRepository = mockk<UserLocationRepository> {
            // `throws` makes the suspend call fail as soon as it's invoked — simulates "no GPS
            // fix", so init{} ends with isLoading = false and a non-null error.
            coEvery { getCurrentLocation() } throws IllegalStateException("No GPS fix available")
            every { hasLocationPermission() } returns true
            every { isLocationEnabled() } returns true
        }
        val compassRepository = mockk<CompassRepository> {
            every { currentPhoneCompassHeadingDegrees() } returns flowOf(0f)
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }
        val qiblaApiRepository = mockk<QiblaApiRepository>()
        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns true
        }
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        viewModel.qiblaState.test {
            val failedState = awaitItem()
            assertFalse(failedState.isLoading)
            assertEquals("No GPS fix available", failedState.error)

            // awaitCancellation() suspends forever, so the retry's fetch is "still in
            // progress" — the state we see next is purely what retry itself reset.
            coEvery { userLocationRepository.getCurrentLocation() } coAnswers { awaitCancellation() }

            viewModel.retryLocationAccess()

            val stateAfterRetry = awaitItem()
            assertTrue(stateAfterRetry.isLoading)
            assertEquals(null, stateAfterRetry.error)
        }
    }

    // Before any bearing exists, qiblaDegrees must be null (not 0.0) so the Kaaba marker
    // isn't drawn at north — assertNull proves the "not fetched yet" state.
    @Test
    fun `qiblaDegrees stays null until the bearing arrives from the API`() = runTest {
        val userLocationRepository = mockk<UserLocationRepository> {
            // coAnswers { awaitCancellation() } never returns a location, so no bearing request
            // can finish — this freezes the ViewModel in the "still waiting for coordinates" state.
            coEvery { getCurrentLocation() } coAnswers { awaitCancellation() }
            every { hasLocationPermission() } returns true
            every { isLocationEnabled() } returns true
        }
        val compassRepository = mockk<CompassRepository> {
            every { currentPhoneCompassHeadingDegrees() } returns flowOf(0f)
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }
        // Bare mock: nothing should call the API before coordinates exist.
        val qiblaApiRepository = mockk<QiblaApiRepository>()
        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns true
        }
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        viewModel.qiblaState.test {
            assertNull(awaitItem().qiblaDegrees)
        }
    }

    @Test
    fun `init fetches the user's location exactly once for both distance and bearing`() = runTest {
        val coordinates = Coordinates(latitude = 40.7128, longitude = -74.0060)

        val userLocationRepository = mockk<UserLocationRepository> {
            coEvery { getCurrentLocation() } returns coordinates
            every { hasLocationPermission() } returns true
            every { isLocationEnabled() } returns true
        }
        val compassRepository = mockk<CompassRepository> {
            every { currentPhoneCompassHeadingDegrees() } returns flowOf(0f)
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns flowOf(DistanceUnit.KILOMETERS)
        }
        val qiblaApiRepository = mockk<QiblaApiRepository> {
            coEvery { fetchQiblaBearing(any(), any()) } returns 136.14
        }
        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns true
        }
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        // coVerify(exactly = 1) { ... } is verify's suspend-function twin: it asserts the mock
        // received that call precisely once.
        // Static location means one GPS fix should feed both the distance and the bearing
        // request, so a second call would mean the ViewModel is fetching per consumer again.
        coVerify(exactly = 1) { userLocationRepository.getCurrentLocation() }
    }

    @Test
    fun `changing the distance unit re-converts the stored km without refetching location`() = runTest {
        val coordinates = Coordinates(latitude = 40.7128, longitude = -74.0060)

        // MutableStateFlow instead of flowOf(): flowOf emits once and completes, but here the
        // test needs to push a *second* value (MILES) after the ViewModel is already running.
        val unitFlow = MutableStateFlow(DistanceUnit.KILOMETERS)

        val userLocationRepository = mockk<UserLocationRepository> {
            coEvery { getCurrentLocation() } returns coordinates
            every { hasLocationPermission() } returns true
            every { isLocationEnabled() } returns true
        }
        val compassRepository = mockk<CompassRepository> {
            every { currentPhoneCompassHeadingDegrees() } returns flowOf(0f)
        }
        val preferencesDataStore = mockk<QiblaPreferencesDataStore> {
            every { observedDistanceUnit() } returns unitFlow
        }
        val qiblaApiRepository = mockk<QiblaApiRepository> {
            coEvery { fetchQiblaBearing(any(), any()) } returns 136.14
        }
        val connectivityProvider = mockk<ConnectivityProvider> {
            every { isOnline() } returns true
        }
        val appDataPreferencesDataStore = mockk<AppDataPreferencesDataStore> {
            coEvery { setIsLocationPermissionGranted(any()) } just Runs
        }

        val viewModel = QiblaViewModel(
            userLocationRepository = userLocationRepository,
            compassRepository = compassRepository,
            preferencesDataStore = preferencesDataStore,
            qiblaApiRepository = qiblaApiRepository,
            connectivityProvider = connectivityProvider,
            appDataPreferencesDataStore = appDataPreferencesDataStore
        )

        viewModel.qiblaState.test {
            // Turbine's test{} collects the StateFlow; the first awaitItem() is the state
            // as it stands after init{} finished (Unconfined dispatcher ran it eagerly).
            val kmState = awaitItem()

            // Pushing a new value makes the ViewModel's unit collector fire, which should
            // emit exactly one new state.
            unitFlow.value = DistanceUnit.MILES
            val milesState = awaitItem()

            assertEquals(DistanceUnit.MILES, milesState.distanceUnit)
            // Same stored km, converted — proves the toggle re-derives from distanceKm
            // (the 0.0001 is assertEquals' tolerance for comparing doubles). Fails today
            // because distanceKm is never populated until refresh() is wired in.
            assertEquals(kmState.distanceKm, milesState.distanceKm, 0.0001)
            assertEquals(QiblaCalculator.kmToMiles(km = kmState.distanceKm), milesState.distanceValue, 0.0001)
            assertTrue(milesState.distanceKm > 0.0)
        }

        // Still just the one fetch from init — the toggle must not trigger another.
        coVerify(exactly = 1) { userLocationRepository.getCurrentLocation() }
    }
}
