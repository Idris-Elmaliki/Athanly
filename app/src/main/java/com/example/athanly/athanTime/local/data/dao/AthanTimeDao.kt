package com.example.athanly.athanTime.local.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.athanly.athanTime.local.data.entity.CityEntity
import com.example.athanly.athanTime.local.data.entity.PrayerScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AthanTimeDao {
    // @Upsert (INSERT ... ON CONFLICT DO UPDATE), not @Insert(REPLACE) — REPLACE deletes then
    // re-inserts the city row, which would cascade-delete its schedule before re-saving it.
    @Upsert
    suspend fun upsertCity(city: CityEntity)

    @Upsert
    suspend fun upsertSchedule(schedule: PrayerScheduleEntity)

    // LIMIT 1 — there's only ever one row now (see City.kt); no id needed to select it.
    @Query("SELECT * FROM cities LIMIT 1")
    fun observeCity(): Flow<CityEntity?>

    @Query("SELECT * FROM prayer_schedules LIMIT 1")
    fun observeSchedule(): Flow<PrayerScheduleEntity?>

    // Default method so both writes commit as one real SQL transaction, not two round-trips.
    @Transaction
    suspend fun upsertCityWithSchedule(
        city: CityEntity,
        schedule: PrayerScheduleEntity
    ) {
        upsertCity(city)
        upsertSchedule(schedule)
    }
}