package com.example.athanly.athanNotifications.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

// AlarmManager alarms don't survive a reboot (system_server resets, wiping them) — this
// re-enqueues PrayerScheduleSyncWorker to run immediately so it re-arms today's alarms right
// away, rather than waiting for its next scheduled midnight run.
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val request = OneTimeWorkRequestBuilder<PrayerScheduleSyncWorker>().build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            PRAYER_SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}