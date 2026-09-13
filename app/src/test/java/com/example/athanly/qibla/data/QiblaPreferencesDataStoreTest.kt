package com.example.athanly.qibla.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.cash.turbine.test
import com.example.athanly.qibla.local.data.QiblaPreferencesDataStore
import com.example.athanly.qibla.local.domain.modelData.enums.DistanceUnit
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class QiblaPreferencesDataStoreTest {

    // Real temp directory, created fresh before each test and deleted after — DataStore
    // needs an actual file to read/write, this gives it one without touching real app storage.
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private fun qiblaPreferencesDataStore(): QiblaPreferencesDataStore {
        val dataStore = PreferenceDataStoreFactory.create(
            produceFile = { temporaryFolder.newFile("test_qibla_preferences.preferences_pb") }
        )
        return QiblaPreferencesDataStore(dataStore = dataStore)
    }

    @Test
    fun `observedDistanceUnit defaults to Kilometers when nothing has been stored yet`() = runTest {
        qiblaPreferencesDataStore().observedDistanceUnit().test {
            assertEquals(DistanceUnit.KILOMETERS, awaitItem())

            // Unlike the mock location Flow, this one never completes on its own — it
            // keeps observing the file for future writes — so we cancel rather than await completion.
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setDistanceUnit persists Miles and observedDistanceUnit reflects it on the next emission`() = runTest {
        val preferencesDataStore = qiblaPreferencesDataStore()

        preferencesDataStore.observedDistanceUnit().test {
            assertEquals(DistanceUnit.KILOMETERS, awaitItem()) // initial default, file is still empty

            preferencesDataStore.setDistanceUnit(DistanceUnit.MILES)

            // edit {} triggers a fresh emission to every active collector of .data
            assertEquals(DistanceUnit.MILES, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}