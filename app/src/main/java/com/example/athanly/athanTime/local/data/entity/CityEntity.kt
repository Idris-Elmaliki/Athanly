package com.example.athanly.athanTime.local.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room-persisted mirror of the City domain model — kept separate so the domain layer
// never depends on Room annotations.
@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey val id: String,

    val name: String,
    val hijriDate: String,
    val latitude: Double,
    val longitude: Double
)