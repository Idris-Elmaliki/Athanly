package com.example.athanly.athanTime.local.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.athanly.athanTime.local.data.dao.AthanTimeDao
import com.example.athanly.athanTime.local.data.entity.CityEntity
import com.example.athanly.athanTime.local.data.entity.PrayerScheduleEntity

// exportSchema = false for now — no migration-testing infra exists yet. Bumped to v3 for
// PrayerScheduleEntity's new latitude/longitude columns (the missing per-city staleness
// signal — see RefreshPrayerScheduleUseCase); AthanDatabaseModule falls back to a destructive
// migration rather than writing a real one, since the app hasn't shipped yet and there's no
// user data worth preserving across this change.
@Database(
    entities = [CityEntity::class, PrayerScheduleEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AthanDatabase : RoomDatabase() {
    abstract fun athanTimeDao(): AthanTimeDao
}