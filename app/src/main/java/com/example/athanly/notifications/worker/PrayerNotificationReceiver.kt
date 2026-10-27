package com.example.athanly.notifications.worker

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.athanly.MainActivity
import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// Fires when one of AlarmSchedulerImpl's exact alarms goes off. Hilt-enabled (field injection —
// BroadcastReceiver doesn't support constructor injection) so it can read which Muadhdhin is
// currently selected and pick that voice's channel.
@AndroidEntryPoint
class PrayerNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var appDataPreferencesDataStore: AppDataPreferencesDataStore

    override fun onReceive(context: Context, intent: Intent) {
        val ordinal = intent.getIntExtra(EXTRA_PRAYER_ORDINAL, -1)
        val prayer = Prayer.entries.getOrNull(ordinal) ?: return

        // Permission can be revoked in system Settings after onboarding originally granted
        // it — checking again here avoids a SecurityException crash on API 33+ if that happened
        val hasNotificationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasNotificationPermission) return

        // onReceive can't be suspend, and reading the selected Muadhdhin needs DataStore's
        // Flow — goAsync() keeps the receiver alive long enough for that one read to finish
        // before the system is allowed to kill it
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val muadhdhin = appDataPreferencesDataStore.observedSelectedMuadhdhin().first()
                postNotification(context, prayer, muadhdhin.id)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun postNotification(
        context: Context,
        prayer: Prayer,
        muadhdhinId: MuadhdhinId
    ) {
        val contentIntent = PendingIntent.getActivity(
            context,
            prayer.ordinal,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = prayerNotificationChannelId(
            muadhdhinId, 
            isFajr = prayer == Prayer.FAJR
        )

        val notification = NotificationCompat.Builder(context, channelId)
            // Flat single-shape vector — the system tints small icons, so a full-color
            // launcher drawable would render as a solid blob in the status bar
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.prayer_notification_title, context.getString(prayer.displayNameRes)))
            .setContentText(context.getString(R.string.prayer_notification_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(context).notify(prayer.ordinal, notification)
    }
}