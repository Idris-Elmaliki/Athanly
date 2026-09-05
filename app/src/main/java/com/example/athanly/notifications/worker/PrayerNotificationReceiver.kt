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
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer

// Fires when one of AlarmSchedulerImpl's exact alarms goes off. No Hilt injection needed —
// everything it needs (which prayer, and static display info) comes off the Intent/resources,
// not a repository, so this stays a plain BroadcastReceiver.
class PrayerNotificationReceiver : BroadcastReceiver() {

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

        val contentIntent = PendingIntent.getActivity(
            context,
            prayer.ordinal,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PRAYER_NOTIFICATION_CHANNEL_ID)
            // Placeholder — ic_launcher_foreground isn't a proper monochrome notification
            // icon yet, revisit once a dedicated status-bar icon asset exists
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.prayer_notification_title, context.getString(prayer.displayNameRes)))
            .setContentText(context.getString(R.string.prayer_notification_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(context).notify(prayer.ordinal, notification)
    }
}