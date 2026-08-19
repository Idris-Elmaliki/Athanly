package com.example.athanly.ui.onBoarding.viewModel

import app.cash.turbine.test
import com.example.athanly.settings.data.SettingsPreferencesDataStore
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class OnBoardingViewModelTest {

    // JUnit4 creates a fresh test class instance per @Test function, so new
    // instances here mean no state leaks between the tests in this file.
    // just Runs stubs setIsHanafiMode (a suspend fun returning Unit) so it can be
    // called without throwing on a strict mockk — it's a write-through fire-and-forget
    // side effect the tests either don't care about or verify via coVerify.
    private val preferencesDataStore = mockk<SettingsPreferencesDataStore> {
        coEvery { setIsHanafiMode(any()) } just Runs
    }
    private val viewModel = OnBoardingViewModel(preferencesDataStore = preferencesDataStore)

    @Test
    fun `notificationsState defaults to permission not granted`() = runTest {
        // Turbine's .test {} subscribes to the StateFlow and lets us step through
        // its emissions one at a time via awaitItem(), instead of manually collecting
        viewModel.notificationsState.test {
            val state = awaitItem() // the StateFlow's current value, emitted immediately
            assertEquals(false, state.isPermissionGranted)
        }
    }

    @Test
    fun `onNotificationPermissionResult(true) updates isPermissionGranted to true`() = runTest {
        viewModel.notificationsState.test {
            awaitItem() // consume the initial default state first

            viewModel.onNotificationPermissionResult(isGranted = true)

            // .update {} on the ViewModel's MutableStateFlow emits a new value,
            // which Turbine surfaces here as the next item to await
            val updated = awaitItem()
            assertEquals(true, updated.isPermissionGranted)
        }
    }

    @Test
    fun `onHanafiModeToggled writes the flipped value through to SettingsPreferencesDataStore`() = runTest {
        // calculationsState starts at isHanafiMode = false, so toggling once should
        // both flip the in-memory state (already covered by existing UI behavior)
        // and persist `true` — this test only checks the persistence half.
        viewModel.onHanafiModeToggled()

        coVerify { preferencesDataStore.setIsHanafiMode(true) }
    }
}