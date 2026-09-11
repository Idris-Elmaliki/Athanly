package com.example.athanly.ui.home.screens.settings.viewModel

import android.media.AudioManager
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
import io.mockk.verify
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

    // Volume Control reads the phone's actual STREAM_NOTIFICATION volume directly rather than
    // a DataStore value, so init{} always calls getStreamMaxVolume/getStreamVolume — every
    // test needs this stubbed regardless of what it's actually checking. 5/10 = 0.5f, an
    // arbitrary non-default fraction so a test can tell it apart from AppDataUiState's 1f default.
    private fun fakeAudioManager(
        currentVolume: Int = 5,
        maxVolume: Int = 10
    ) = mockk<AudioManager> {
        every { getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION) } returns maxVolume
        every { getStreamVolume(AudioManager.STREAM_NOTIFICATION) } returns currentVolume
        every { setStreamVolume(any(), any(), any()) } just Runs
    }

    @Test
    fun `settingsState combines all four preference flows from the DataStore, plus the phone's actual notification volume`() = runTest {
        val kuwait = muadhhinList.first { it.id == MuadhdhinId.ISTANBUL }

        // mockk<T> { every { ... } returns ... } builds a fake AppDataPreferencesDataStore
        // where every observed-preference call returns a hardcoded flow we control,
        // instead of running a real DataStore file.
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(true)
            every { observedIsHanafiMode() } returns flowOf(true)
            every { observedLanguage() } returns flowOf(Language.Arabic)
            every { observedSelectedMuadhdhin() } returns flowOf(kuwait)
        }

        val viewModel = SettingsViewModel(
            preferencesDataStore = preferencesDataStore,
            audioManager = fakeAudioManager(currentVolume = 5, maxVolume = 10)
        )

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
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            coEvery { setIs24HourFormat(any()) } just Runs
        }

        val viewModel = SettingsViewModel(
            preferencesDataStore = preferencesDataStore,
            audioManager = fakeAudioManager()
        )

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
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            coEvery { setIsHanafiMode(any()) } just Runs
        }

        val viewModel = SettingsViewModel(
            preferencesDataStore = preferencesDataStore,
            audioManager = fakeAudioManager()
        )

        viewModel.onHanafiModeToggled()

        coVerify { preferencesDataStore.setIsHanafiMode(true) }
    }

    @Test
    fun `onAthanVolumeChanged sets the phone's actual notification stream volume`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(false)
            every { observedIsHanafiMode() } returns flowOf(false)
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
        }
        val audioManager = fakeAudioManager(currentVolume = 10, maxVolume = 10)

        val viewModel = SettingsViewModel(
            preferencesDataStore = preferencesDataStore,
            audioManager = audioManager
        )

        // Unlike the two toggles above, this one takes the new value directly — a slider
        // reports whatever position the user dragged it to, there's nothing to flip.
        // 0.25f of a max of 10 rounds to stream index 3 (2.5 rounds up).
        viewModel.onAthanVolumeChanged(volume = 0.25f)

        verify { audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 3, 0) }
    }

    @Test
    fun `onLanguageSelected writes the given language through to the DataStore`() = runTest {
        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(false)
            every { observedIsHanafiMode() } returns flowOf(false)
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            coEvery { setLanguage(any()) } just Runs
        }

        val viewModel = SettingsViewModel(
            preferencesDataStore = preferencesDataStore,
            audioManager = fakeAudioManager()
        )

        // Like the slider, the dropdown reports whichever option the user picked directly.
        viewModel.onLanguageSelected(language = Language.Arabic)

        coVerify { preferencesDataStore.setLanguage(Language.Arabic) }
    }

    @Test
    fun `onMuadhdhinSelected writes the given Muadhdhin through to the DataStore`() = runTest {
        val kuwait = muadhhinList.first { it.id == MuadhdhinId.ISTANBUL }

        val preferencesDataStore = mockk<AppDataPreferencesDataStore> {
            every { observedIs24HourFormat() } returns flowOf(false)
            every { observedIsHanafiMode() } returns flowOf(false)
            every { observedLanguage() } returns flowOf(Language.English)
            every { observedSelectedMuadhdhin() } returns flowOf(muadhhinList.first())
            coEvery { setSelectedMuadhdhin(any()) } just Runs
        }

        val viewModel = SettingsViewModel(
            preferencesDataStore = preferencesDataStore,
            audioManager = fakeAudioManager()
        )

        viewModel.onMuadhdhinSelected(muadhdhin = kuwait)

        coVerify { preferencesDataStore.setSelectedMuadhdhin(kuwait) }
    }
}