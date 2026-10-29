package com.example.athanly.ui.viewModel

import com.example.athanly.appData.domain.modelData.Language
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AppViewModelTest {

    // StandardTestDispatcher, unlike the UnconfinedTestDispatcher other ViewModel tests
    // use, does NOT run launched coroutines eagerly — this ViewModel's whole purpose is
    // the null-while-loading state, so the test needs to control exactly when the init{}
    // coroutine runs instead of it resolving before we can observe the loading state.
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `hasCompletedOnboarding starts as null before the DataStore value loads`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedHasCompletedOnboarding() } returns flowOf(true)
        }

        val viewModel = AppViewModel(preferencesDataStore = preferencesDataStore)

        // init{}'s coroutine hasn't run yet on StandardTestDispatcher until advanced
        assertEquals(null, viewModel.hasCompletedOnboarding.value)
    }

    @Test
    fun `hasCompletedOnboarding resolves to true once the DataStore says onboarding is complete`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedHasCompletedOnboarding() } returns flowOf(true)
        }

        val viewModel = AppViewModel(preferencesDataStore = preferencesDataStore)
        advanceUntilIdle()

        assertEquals(true, viewModel.hasCompletedOnboarding.value)
    }

    @Test
    fun `hasCompletedOnboarding resolves to false when onboarding hasn't been completed`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedHasCompletedOnboarding() } returns flowOf(false)
        }

        val viewModel = AppViewModel(preferencesDataStore = preferencesDataStore)
        advanceUntilIdle()

        assertEquals(false, viewModel.hasCompletedOnboarding.value)
    }

    @Test
    fun `language resolves to Arabic once the DataStore says Arabic`() = runTest {
        // mockk<T> { every { call() } returns value } builds a fake DataStore whose Flows
        // emit exactly what we script — no real file or Android needed. Both Flows are
        // stubbed because the ViewModel's init{} subscribes to each of them.
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedHasCompletedOnboarding() } returns flowOf(true)
            every { observedLanguage() } returns flowOf(Language.Arabic)
        }

        val viewModel = AppViewModel(preferencesDataStore = preferencesDataStore)
        // StandardTestDispatcher queues init{}'s coroutine; advanceUntilIdle() runs it, so
        // the Arabic emission has reached the StateFlow by the time we read .value
        advanceUntilIdle()

        // Red now: AppViewModel has no `language` property yet, so this doesn't compile.
        // Making it compile and pass is the next step.
        assertEquals(Language.Arabic, viewModel.language.value)
    }
}