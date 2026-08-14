package com.example.athanly.settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.athanly.settings.data.SettingsPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Must live at top level (outside the module object) — this delegate binds to the
// Context class itself, not to any one instance of it.
private val Context.settingsPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_preferences")

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideSettingsPreferencesDataStore(
        @ApplicationContext context: Context
    ): SettingsPreferencesDataStore {
        return SettingsPreferencesDataStore(
            dataStore = context.settingsPreferencesDataStore
        )
    }
}