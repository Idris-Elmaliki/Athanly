package com.example.athanly.ui.home.screens.settings.viewModel

import app.cash.turbine.test
import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId
import com.example.athanly.appData.domain.modelData.muadhhinList
import com.example.athanly.appData.domain.modelData.Language
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.ui.home.viewModel.SettingsViewModel
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

class SettingsViewModelTest {

    // Same reasoning as QiblaViewModelTest: viewModelScope needs Dispatchers.Main
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
    fun `settingsState combines all five preference flows from the DataStore`() = runTest {
        val kuwait = muadhhinList.first { it.id == MuadhdhinId.KUWAIT }

        // mockk<T> { every { ... } returns ... } builds a fake AppDataPreferencesDataStore
        // where every observed-preference call returns a hardcoded flow we control,
        // instead of running a real DataStore file.
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(true)
            every { observedIsHanafiMode() } returns flowOf(true)
            every { observedAthanVolume() } returns flowOf(0.5f)
            every { observedLanguage() } returns flowOf(Language.Arabic)
            every { observedSelectedMuadhdhin() } returns flowOf(kuwait)
        }

        val viewModel = SettingsViewModel(preferencesDataStore = preferencesDataStore)

        viewModel.settingsState.test {
            val state = awaitItem()

            assertEquals(true, state.is24HourFormat)
            assertEquals(true, state.isHanafiMode)
            assertEquals(0.5f, state.athanVolume)
            assertEquals(Language.Arabic, state.selectedLanguage)
            assertEquals(kuwait, state.selectedMuadhdhin)
        }
    }

    @Test
    fun `onTimeFormatToggled writes the flipped 24-hour-format value through to the DataStore`() = runTest {
        // coEvery { ... } just Runs stubs a suspend function that returns Unit (setIs24HourFormat
        // is `suspend fun ... : Unit`) — without this, calling it on a strict mockk would throw,
        // since only the observed*() getters below are stubbed with `every`.
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(false)
            every { observedIsHanafiMode() } returns flowOf(false)
            every { observedAthanVolume() } returns flowOf(1f)
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            coEvery { setIs24HourFormat(any()) } just Runs
        }

        val viewModel = SettingsViewModel(preferencesDataStore = preferencesDataStore)

        viewModel.onTimeFormatToggled()

        // coVerify { ... } is coEvery's counterpart for assertions: it confirms a suspend
        // function was actually called with these exact arguments, instead of stubbing
        // a return value — here, that onTimeFormatToggled() flips false to true rather
        // than reading it back through settingsState (which stays false in this test,
        // since the mocked observedIs24HourFormat() flow never changes).
        coVerify { preferencesDataStore.setIs24HourFormat(true) }
    }

    @Test
    fun `onHanafiModeToggled writes the flipped Hanafi-mode value through to the DataStore`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(false)
            every { observedIsHanafiMode() } returns flowOf(false)
            every { observedAthanVolume() } returns flowOf(1f)
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            coEvery { setIsHanafiMode(any()) } just Runs
        }

        val viewModel = SettingsViewModel(preferencesDataStore = preferencesDataStore)

        viewModel.onHanafiModeToggled()

        coVerify { preferencesDataStore.setIsHanafiMode(true) }
    }

    @Test
    fun `onAthanVolumeChanged writes the given volume through to the DataStore`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(false)
            every { observedIsHanafiMode() } returns flowOf(false)
            every { observedAthanVolume() } returns flowOf(1f)
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            coEvery { setAthanVolume(any()) } just Runs
        }

        val viewModel = SettingsViewModel(preferencesDataStore = preferencesDataStore)

        // Unlike the two toggles above, this one takes the new value directly — a slider
        // reports whatever position the user dragged it to, there's nothing to flip.
        viewModel.onAthanVolumeChanged(volume = 0.25f)

        coVerify { preferencesDataStore.setAthanVolume(0.25f) }
    }

    @Test
    fun `onLanguageSelected writes the given language through to the DataStore`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(false)
            every { observedIsHanafiMode() } returns flowOf(false)
            every { observedAthanVolume() } returns flowOf(1f)
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            coEvery { setLanguage(any()) } just Runs
        }

        val viewModel = SettingsViewModel(preferencesDataStore = preferencesDataStore)

        // Like the slider, the dropdown reports whichever option the user picked directly.
        viewModel.onLanguageSelected(language = Language.Arabic)

        coVerify { preferencesDataStore.setLanguage(Language.Arabic) }
    }

    @Test
    fun `onMuadhdhinSelected writes the given Muadhdhin through to the DataStore`() = runTest {
        val kuwait = muadhhinList.first { it.id == MuadhdhinId.KUWAIT }

        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(false)
            every { observedIsHanafiMode() } returns flowOf(false)
            every { observedAthanVolume() } returns flowOf(1f)
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            coEvery { setSelectedMuadhdhin(any()) } just Runs
        }

        val viewModel = SettingsViewModel(preferencesDataStore = preferencesDataStore)

        viewModel.onMuadhdhinSelected(muadhdhin = kuwait)

        coVerify { preferencesDataStore.setSelectedMuadhdhin(kuwait) }
    }
}
