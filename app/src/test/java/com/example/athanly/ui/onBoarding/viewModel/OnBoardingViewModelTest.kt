package com.example.athanly.ui.onBoarding.viewModel

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class OnBoardingViewModelTest {

    // JUnit4 creates a fresh test class instance per @Test function, so a new
    // ViewModel here means no state leaks between the two tests below
    private val viewModel = OnBoardingViewModel()

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
}