package com.example.athanly.athanTime.local.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// cityId is both the primary key and the foreign key — enforces "one cached schedule per
// city" at the schema level, and cascades delete when the city is removed.
// Times/date are stored as ISO-8601 Strings (converted explicitly in the repository mapper,
// not via Room TypeConverters) since Room has no native java.time support.
// `date` is the freshness signal: a future refresh check compares this against today's date
// to decide whether the Athan API needs to be called again for this city.

@Entity(
    tableName = "prayer_schedules",
    foreignKeys = [
        ForeignKey(
            entity = CityEntity::class,
            parentColumns = ["id"],
            childColumns = ["cityId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PrayerScheduleEntity(
    @PrimaryKey val cityId: String,
    val date: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,

    // Which Asr calculation mode this row was computed with — compared against DataStore's
    // live Hanafi setting to catch a mode flip as its own staleness signal
    val isHanafiMode: Boolean
)