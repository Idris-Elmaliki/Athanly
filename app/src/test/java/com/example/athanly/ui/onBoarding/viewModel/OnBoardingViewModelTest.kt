package com.example.athanly.ui.onBoarding.viewModel

import app.cash.turbine.test
import com.example.athanly.appData.domain.modelData.Language
import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId
import com.example.athanly.appData.domain.modelData.muadhhinList
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OnBoardingViewModelTest {

    // Same reasoning as SettingsViewModelTest: viewModelScope needs Dispatchers.Main
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
    fun `appDataState combines language, selectedMuadhdhin, and isHanafiMode from the DataStore`() = runTest {
        val kuwait = muadhhinList.first { it.id == MuadhdhinId.KUWAIT }

        // Every onboarding screen that used to have its own local UiState (Welcome,
        // AthanSelection, Calculations) now reads straight off this shared state instead,
        // so the ViewModel needs all three of its source flows stubbed even in a single test.
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedLanguage() } returns flowOf(Language.Arabic)
            every { observedSelectedMuadhdhin() } returns flowOf(kuwait)
            every { observedIsHanafiMode() } returns flowOf(true)
        }

        val viewModel = OnBoardingViewModel(preferencesDataStore = preferencesDataStore)

        viewModel.appDataState.test {
            val state = awaitItem()

            assertEquals(Language.Arabic, state.selectedLanguage)
            assertEquals(kuwait, state.selectedMuadhdhin)
            assertEquals(true, state.isHanafiMode)
        }
    }

    @Test
    fun `onLanguageSelected writes the given language through to the DataStore`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            every { observedIsHanafiMode() } returns flowOf(false)
            coEvery { setLanguage(any()) } just Runs
        }

        val viewModel = OnBoardingViewModel(preferencesDataStore = preferencesDataStore)

        viewModel.onLanguageSelected(language = Language.Arabic)

        coVerify { preferencesDataStore.setLanguage(Language.Arabic) }
    }

    @Test
    fun `onMuadhhinSelected writes the given Muadhdhin through to the DataStore`() = runTest {
        val kuwait = muadhhinList.first { it.id == MuadhdhinId.KUWAIT }

        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            every { observedIsHanafiMode() } returns flowOf(false)
            coEvery { setSelectedMuadhdhin(any()) } just Runs
        }

        val viewModel = OnBoardingViewModel(preferencesDataStore = preferencesDataStore)

        viewModel.onMuadhhinSelected(muadhdhin = kuwait)

        coVerify { preferencesDataStore.setSelectedMuadhdhin(kuwait) }
    }

    @Test
    fun `onHanafiModeToggled writes the flipped value through to the DataStore`() = runTest {
        // isHanafiMode observed as false, so toggling once should write true.
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            every { observedIsHanafiMode() } returns flowOf(false)
            coEvery { setIsHanafiMode(any()) } just Runs
        }

        val viewModel = OnBoardingViewModel(preferencesDataStore = preferencesDataStore)

        viewModel.onHanafiModeToggled()

        coVerify { preferencesDataStore.setIsHanafiMode(true) }
    }

    @Test
    fun `onOnboardingComplete writes true through to the DataStore`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            every { observedIsHanafiMode() } returns flowOf(false)
            coEvery { setHasCompletedOnboarding(any()) } just Runs
        }

        val viewModel = OnBoardingViewModel(preferencesDataStore = preferencesDataStore)

        viewModel.onOnboardingComplete()

        coVerify { preferencesDataStore.setHasCompletedOnboarding(true) }
    }

    @Test
    fun `notificationsState defaults to permission not granted`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            every { observedIsHanafiMode() } returns flowOf(false)
        }

        val viewModel = OnBoardingViewModel(preferencesDataStore = preferencesDataStore)

        // Turbine's .test {} subscribes to the StateFlow and lets us step through
        // its emissions one at a time via awaitItem(), instead of manually collecting
        viewModel.notificationsState.test {
            val state = awaitItem() // the StateFlow's current value, emitted immediately
            assertEquals(false, state.isPermissionGranted)
        }
    }

    @Test
    fun `onNotificationPermissionResult(true) updates isPermissionGranted to true`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            every { observedIsHanafiMode() } returns flowOf(false)
        }

        val viewModel = OnBoardingViewModel(preferencesDataStore = preferencesDataStore)

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