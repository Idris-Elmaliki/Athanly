package com.example.athanly

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.athanly.notifications.worker.PRAYER_SYNC_WORK_NAME
import com.example.athanly.notifications.worker.NotificationChannelInitializer
import com.example.athanly.notifications.worker.PrayerScheduleSyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AthanlyApplication : Application(), Configuration.Provider {

    // Hilt-provided factory so PrayerScheduleSyncWorker (a @HiltWorker) can receive
    // @Inject dependencies instead of needing a no-arg constructor
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationChannelInitializer: NotificationChannelInitializer

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        notificationChannelInitializer.initialize()

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