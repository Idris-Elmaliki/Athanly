package com.example.athanly.notifications.worker

import com.example.athanly.athanTime.domain.PrayerSchedule

// Schedules/cancels the OS-level alarms that fire each of the day's 5 prayer notifications.
// Kept behind this interface (implemented by AlarmSchedulerImpl with AlarmManager) so
// PrayerScheduleSyncWorker doesn't depend on Android alarm APIs directly.
interface AlarmScheduler {
    fun scheduleAll(schedule: PrayerSchedule)
    fun cancelAll()
}