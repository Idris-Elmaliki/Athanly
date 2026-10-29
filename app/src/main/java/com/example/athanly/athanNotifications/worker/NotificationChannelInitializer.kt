package com.example.athanly.athanNotifications.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.content.getSystemService
import com.example.athanly.R
import com.example.athanly.appData.data.localizedFor
import com.example.athanly.appData.domain.modelData.Language
import com.example.athanly.appData.domain.modelData.muadhhinList
import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// A channel's sound is fixed at creation and can't be swapped later, so every Muadhdhin needs
// its own Fajr + shared channel — PrayerNotificationReceiver calls this with the currently
// selected Muadhdhin to know which pair to post to.
fun prayerNotificationChannelId(muadhdhinId: MuadhdhinId, isFajr: Boolean): String {
    val suffix = if (isFajr) "fajr" else "other"
    return "prayer_${muadhdhinId.name.lowercase()}_$suffix"
}

// Creates a Fajr + shared channel for every Muadhdhin (not just the selected one) so switching
// the selection in Settings never needs to create a channel on the fly. Re-registering an
// existing channel only refreshes its name and description (the sound stays fixed), which is
// exactly what a language change needs — so this is safe to call on every app start and
// every language switch.
class NotificationChannelInitializer @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun initialize(language: Language) {
        // NotificationChannel/NotificationManager.createNotificationChannel don't exist
        // below API 26 — notifications on older devices have no channel concept
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return

        val notificationManager = context.getSystemService<NotificationManager>() ?: return

        // CONTENT_TYPE_MUSIC (not SONIFICATION) so the system doesn't duck/shorten playback
        // the way it would a short UI ping — an Athan recording runs a couple minutes
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        // The application context follows the device language; channel names are shown in
        // system Settings, so they need the in-app language to match everything else
        val localizedContext = context.localizedFor(language)
        val channelDescription = localizedContext.getString(R.string.prayer_notification_channel_description)

        muadhhinList.forEach { muadhdhin ->
            val locationName = localizedContext.getString(muadhdhin.locationNameRes)

            createChannel(
                notificationManager = notificationManager,
                channelId = prayerNotificationChannelId(muadhdhin.id, isFajr = true),
                channelName = localizedContext.getString(R.string.prayer_notification_channel_name_fajr, locationName),
                channelDescription = channelDescription,
                audioRes = muadhdhin.fajrAudioRes,
                audioAttributes = audioAttributes
            )
            createChannel(
                notificationManager = notificationManager,
                channelId = prayerNotificationChannelId(muadhdhin.id, isFajr = false),
                channelName = localizedContext.getString(R.string.prayer_notification_channel_name_other, locationName),
                channelDescription = channelDescription,
                audioRes = muadhdhin.otherAudioRes,
                audioAttributes = audioAttributes
            )
        }
    }

    private fun createChannel(
        notificationManager: NotificationManager,
        channelId: String,
        channelName: String,
        channelDescription: String,
        audioRes: Int,
        audioAttributes: AudioAttributes
    ) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = channelDescription
            setSound(
                Uri.parse("android.resource://${context.packageName}/$audioRes"),
                audioAttributes
            )
        }

        notificationManager.createNotificationChannel(channel)
    }
}