package com.example.athanly.notifications.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.athanTime.domain.RefreshPrayerScheduleUseCase
import com.example.athanly.athanTime.local.domain.repository.AthanTimeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

// Enqueued under this name so re-enqueuing (app start, BootCompletedReceiver, this worker's
// own self-chaining) always REPLACEs the same pending job instead of stacking duplicates
const val PRAYER_SYNC_WORK_NAME = "prayer_schedule_sync"

// Runs once a day: keeps the cached schedule fresh (same staleness check HomeViewModel uses)
// and re-arms tomorrow's alarms against whatever schedule ends up in Room, then re-enqueues
// itself for the next midnight. @HiltWorker + @AssistedInject is how a WorkManager Worker
// gets @Inject dependencies — WorkManager itself only knows how to supply Context/WorkerParameters,
// so HiltWorkerFactory (wired in AthanlyApplication) fills in the rest.
@HiltWorker
class PrayerScheduleSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val athanTimeRepository: AthanTimeRepository,
    private val preferencesDataStore: AppDataPreferencesDataStore,
    private val refreshPrayerScheduleUseCase: RefreshPrayerScheduleUseCase,
    private val alarmScheduler: AlarmScheduler
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            // No city yet (onboarding hasn't run) — nothing to sync, but still re-enqueue for
            // tomorrow so this keeps checking rather than only ever running once
            val city = athanTimeRepository.observeCity().first()
            if (city != null) {
                val isHanafiMode = preferencesDataStore.observedIsHanafiMode().first()
                refreshPrayerScheduleUseCase(
                    city = city,
                    today = LocalDate.now(),
                    isHanafiMode = isHanafiMode
                )

                // Re-reads after the refresh rather than trusting a fetched-just-now value,
                // since a same-day cache hit means refreshPrayerScheduleUseCase never fetched
                // anything new — either way, whatever's in Room now is what should be armed
                athanTimeRepository.observeSchedule().first()?.let { schedule ->
                    alarmScheduler.scheduleAll(schedule)
                }
            }

            scheduleNextRun()
            Result.success()
        } catch (e: Exception) {
            // WorkManager's own retry/backoff policy handles this — no self-chaining here,
            // so a failure doesn't also leave a duplicate midnight run queued up
            Result.retry()
        }
    }

    private fun scheduleNextRun() {
        val delayUntilMidnight = Duration.between(
            LocalDateTime.now(),
            LocalDate.now().plusDays(1).atStartOfDay()
        )

        val request = OneTimeWorkRequestBuilder<PrayerScheduleSyncWorker>()
            .setInitialDelay(delayUntilMidnight.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            PRAYER_SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}