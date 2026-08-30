package com.example.athanly.athanTime.local.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.athanly.athanTime.local.data.dao.AthanTimeDao
import com.example.athanly.athanTime.local.data.entity.CityEntity
import com.example.athanly.athanTime.local.data.entity.PrayerScheduleEntity

// exportSchema = false for now — no migration-testing infra exists yet and this is v1;
// revisit once real schema migrations start mattering.
@Database(
    entities = [CityEntity::class, PrayerScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AthanDatabase : RoomDatabase() {
    abstract fun athanTimeDao(): AthanTimeDao
}