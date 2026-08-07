package com.example.athanly.qibla.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.athanly.qibla.domain.local.modelData.enums.DistanceUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Takes DataStore<Preferences> directly (not a Context it builds one from) so tests
// can hand it a real temp-file-backed instance — QiblaModule wires up the actual
// Context-bound DataStore for the running app.
class QiblaPreferencesDataStore(
    private val dataStore: DataStore<Preferences>
) {
    // DistanceUnit has no native DataStore type, so it's stored as its .name string.
    // Falls back to Kilometers if the key is missing (first launch) or unparseable.
    fun observedDistanceUnit(): Flow<DistanceUnit> {
        return dataStore.data.map { preferences ->
            val storedName = preferences[DISTANCE_UNIT_KEY]
            
            storedName?.let {
                runCatching { DistanceUnit.valueOf(it) }.getOrNull()
            } ?: DistanceUnit.KILOMETERS
        }
    }

    suspend fun setDistanceUnit(unit: DistanceUnit) {
        dataStore.edit { preferences ->
            preferences[DISTANCE_UNIT_KEY] = unit.name
        }
    }

    companion object {
        private val DISTANCE_UNIT_KEY = stringPreferencesKey("distance_unit")
    }
}