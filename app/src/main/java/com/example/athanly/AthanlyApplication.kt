package com.example.athanly

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.athanNotifications.worker.PRAYER_SYNC_WORK_NAME
import com.example.athanly.athanNotifications.worker.NotificationChannelInitializer
import com.example.athanly.athanNotifications.worker.PrayerScheduleSyncWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AthanlyApplication : Application(), Configuration.Provider {
    // Hilt-provided factory so PrayerScheduleSyncWorker (a @HiltWorker) can receive
    // @Inject dependencies instead of needing a no-arg constructor
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationChannelInitializer: NotificationChannelInitializer

    @Inject
    lateinit var appDataPreferencesDataStore: AppDataPreferencesDataStore

    // Lives as long as the process — channel names must be refreshed on a language change
    // even if no Activity is open, so this can't be tied to a ViewModel or Activity scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Emits the stored language at startup and again on every switch, so one collector
        // covers both creating the channels and renaming them in the new language
        applicationScope.launch {
            try {
                appDataPreferencesDataStore.observedLanguage().collect { language ->
                    notificationChannelInitializer.initialize(language)
                }
            } catch (e: Exception) {
                // Channels keep their last names; a failed rename shouldn't crash app startup
            }
        }

        // ExistingWorkPolicy.KEEP — every process start calls this, but a fresh app open
        // during the day shouldn't cancel/replace a chain already scheduled for tonight's
        // midnight (or by BootCompletedReceiver). This only actually enqueues anything on a
        // fresh install, before any chain exists yet.
        WorkManager.getInstance(this).enqueueUniqueWork(
            PRAYER_SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<PrayerScheduleSyncWorker>().build()
        )
    }
}