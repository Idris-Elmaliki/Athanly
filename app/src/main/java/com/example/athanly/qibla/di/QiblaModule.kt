package com.example.athanly.qibla.di

import android.content.Context
import android.hardware.SensorManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.athanly.qibla.data.MockUserLocationRepository
import com.example.athanly.qibla.data.QiblaPreferencesDataStore
import com.example.athanly.qibla.data.SensorCompassRepository
import com.example.athanly.qibla.domain.local.repository.CompassRepository
import com.example.athanly.qibla.domain.local.repository.UserLocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Must live at top level (outside the module object) — this delegate binds to the
// Context class itself, not to any one instance of it.
private val Context.qiblaPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "qibla_preferences")

@Module
@InstallIn(SingletonComponent::class)
object QiblaModule {

    // Mock-backed today; swapping to a real GPS-backed UserLocationRepository later is
    // just changing this one line — QiblaViewModel only ever depends on the interface.
    @Provides
    @Singleton
    fun provideUserLocationRepository(): UserLocationRepository = MockUserLocationRepository()

    @Provides
    @Singleton
    fun provideCompassRepository(@ApplicationContext context: Context): CompassRepository {
        return SensorCompassRepository(
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        )
    }

    @Provides
    @Singleton
    fun provideQiblaPreferencesDataStore(
        @ApplicationContext context: Context
    ): QiblaPreferencesDataStore {
        return QiblaPreferencesDataStore(
            dataStore = context.qiblaPreferencesDataStore
        )
    }
}