package com.example.athanly.appData.di

import android.content.Context
import android.media.AudioManager
import androidx.core.content.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.athanly.appData.data.AthanPreviewPlayerImpl
import com.example.athanly.appData.domain.repository.AthanPreviewPlayer
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Must live at top level (outside the module) — this delegate binds to the
// Context class itself, not to any one instance of it.
private val Context.settingsPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_preferences")

// abstract class, not object — @Binds (interface-to-impl) requires an abstract function,
// which an object can't declare, so the DataStore-providing @Provides function moves into
// a companion object instead. Mirrors LocationModule's shape.
@Module
@InstallIn(SingletonComponent::class)
abstract class AppDataModule {
    @Binds
    @Singleton
    abstract fun bindAthanPreviewPlayer(impl: AthanPreviewPlayerImpl): AthanPreviewPlayer

    companion object {
        @Provides
        @Singleton
        fun provideSettingsPreferencesDataStore(
            @ApplicationContext context: Context
        ): AppDataPreferencesDataStore {
            return AppDataPreferencesDataStore(
                dataStore = context.settingsPreferencesDataStore
            )
        }

        // Volume Control on Settings drives this directly (STREAM_NOTIFICATION — the stream
        // the Athan notification channel's sound actually plays on) instead of a custom
        // DataStore-backed value, so it reflects/controls the phone's real volume.
        @Provides
        @Singleton
        fun provideAudioManager(
            @ApplicationContext context: Context
        ): AudioManager {
            return context.getSystemService<AudioManager>()!!
        }
    }
}