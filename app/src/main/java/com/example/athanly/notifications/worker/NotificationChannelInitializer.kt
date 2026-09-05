package com.example.athanly.notifications.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import com.example.athanly.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// Referenced here and by PrayerNotificationReceiver when it builds each prayer's notification
const val PRAYER_NOTIFICATION_CHANNEL_ID = "prayer_times"

// Creates the app's one notification channel. Registering is a no-op if the channel already
// exists, so this is safe to call on every app start rather than only once ever.
class NotificationChannelInitializer @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun initialize() {
        // NotificationChannel/NotificationManager.createNotificationChannel don't exist
        // below API 26 — notifications on older devices have no channel concept
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) 
            return

        val channel = NotificationChannel(
            PRAYER_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.prayer_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.prayer_notification_channel_description)
        }

        context.getSystemService<NotificationManager>()?.createNotificationChannel(channel)
    }
}