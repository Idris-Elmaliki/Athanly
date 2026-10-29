package com.example.athanly.athanNotifications.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.example.athanly.athanTime.domain.PrayerSchedule
import com.example.athanly.athanTime.domain.timeFor
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Clock
import java.time.ZoneId
import javax.inject.Inject

// Extra key PrayerNotificationReceiver reads off the broadcast Intent to know which prayer fired
const val EXTRA_PRAYER_ORDINAL = "prayer_ordinal"

class AlarmSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val clock: Clock
) : AlarmScheduler {
    private val alarmManager = context.getSystemService<AlarmManager>()

    override fun scheduleAll(schedule: PrayerSchedule) {
        // Sunrise isn't a prayer — isPrayer filters it out so it never gets an alarm
        Prayer.entries.filter { it.isPrayer }.forEach { prayer ->
            val triggerAtMillis = schedule.timeFor(prayer)
                .atDate(schedule.date)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            // setExactAndAllowWhileIdle fires IMMEDIATELY for a trigger time already in the
            // past — without this check, the first time a schedule is armed mid-day (e.g. the
            // user finishes onboarding at 3pm) every already-passed prayer for today would fire
            // as an instant notification burst. Skipping it here just leaves that one prayer
            // un-alarmed for the rest of today; tomorrow's sync arms it normally.
            if (triggerAtMillis > clock.millis()) {
                schedule(prayer, triggerAtMillis)
            }
        }
    }

    override fun cancelAll() {
        val manager = alarmManager ?: return
        Prayer.entries.filter { it.isPrayer }.forEach { prayer ->
            manager.cancel(pendingIntentFor(prayer))
        }
    }

    private fun schedule(
        prayer: Prayer,
        triggerAtMillis: Long
    ) {
        val manager = alarmManager ?: return
        val pendingIntent = pendingIntentFor(prayer)

        if (canScheduleExact(manager)) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            // Degrades precision instead of crashing when the user hasn't granted the
            // exact-alarm special permission
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun canScheduleExact(manager: AlarmManager): Boolean {
        // canScheduleExactAlarms() only exists on API 31+ — exact alarms were unrestricted
        // before that permission was introduced, so older devices always qualify
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            manager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun pendingIntentFor(prayer: Prayer): PendingIntent {
        val intent = Intent(
            context,
            PrayerNotificationReceiver::class.java
        ).apply {
            putExtra(
                EXTRA_PRAYER_ORDINAL,
                prayer.ordinal
            )
        }

        // requestCode = prayer.ordinal keeps each prayer's PendingIntent distinct so
        // scheduling one doesn't overwrite another
        return PendingIntent.getBroadcast(
            context,
            prayer.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}