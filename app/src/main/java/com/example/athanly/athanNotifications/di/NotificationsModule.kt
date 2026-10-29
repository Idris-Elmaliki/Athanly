package com.example.athanly.athanNotifications.di

import com.example.athanly.athanNotifications.worker.AlarmScheduler
import com.example.athanly.athanNotifications.worker.AlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// NotificationChannelInitializer/AlarmSchedulerImpl/PrayerScheduleSyncWorker all already carry
// @Inject constructors — Hilt can build them directly. AlarmScheduler is the one thing that
// needs a module at all: it's an interface, so Hilt needs @Binds to know AlarmSchedulerImpl
// is the implementation to hand out.
@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {
    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(impl: AlarmSchedulerImpl): AlarmScheduler
}