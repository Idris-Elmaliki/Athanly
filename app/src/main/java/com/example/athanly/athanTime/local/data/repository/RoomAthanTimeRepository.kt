package com.example.athanly.athanTime.local.data.repository

import com.example.athanly.athanTime.local.data.dao.AthanTimeDao
import com.example.athanly.athanTime.local.data.entity.CityEntity
import com.example.athanly.athanTime.local.data.entity.PrayerScheduleEntity
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.domain.PrayerSchedule
import com.example.athanly.athanTime.local.domain.repository.AthanTimeRepository
import com.example.athanly.userInfoCalculations.toIsoString
import com.example.athanly.userInfoCalculations.toLocalDate
import com.example.athanly.userInfoCalculations.toLocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// The domain City has no id (there's only ever one) — this constant is the Room-only
// primary key that lets the single row still satisfy @PrimaryKey/@ForeignKey, without
// leaking past this data-layer mapper.
private const val CURRENT_LOCATION_ID = "current_location"

class RoomAthanTimeRepository @Inject constructor(
    private val dao: AthanTimeDao
) : AthanTimeRepository {

    override fun observeCity(): Flow<City?> {
        return dao.observeCity().map { it?.toDomain() }
    }

    override fun observeSchedule(): Flow<PrayerSchedule?> {
        return dao.observeSchedule().map { it?.toDomain() }
    }

    override suspend fun saveCity(city: City) {
        dao.upsertCity(city.toEntity())
    }

    override suspend fun saveCityWithSchedule(city: City, schedule: PrayerSchedule) {
        dao.upsertCityWithSchedule(city.toEntity(), schedule.toEntity())
    }
}

private fun CityEntity.toDomain() = City(
    name = name,
    hijriDate = hijriDate,
    latitude = latitude,
    longitude = longitude
)

private fun City.toEntity() = CityEntity(
    id = CURRENT_LOCATION_ID,
    name = name,
    hijriDate = hijriDate,
    latitude = latitude,
    longitude = longitude
)

private fun PrayerScheduleEntity.toDomain() = PrayerSchedule(
    date = date.toLocalDate(),
    fajr = fajr.toLocalTime(),
    sunrise = sunrise.toLocalTime(),
    dhuhr = dhuhr.toLocalTime(),
    asr = asr.toLocalTime(),
    maghrib = maghrib.toLocalTime(),
    isha = isha.toLocalTime(),
    isHanafiMode = isHanafiMode
)

private fun PrayerSchedule.toEntity() = PrayerScheduleEntity(
    cityId = CURRENT_LOCATION_ID,
    date = date.toIsoString(),
    fajr = fajr.toIsoString(),
    sunrise = sunrise.toIsoString(),
    dhuhr = dhuhr.toIsoString(),
    asr = asr.toIsoString(),
    maghrib = maghrib.toIsoString(),
    isha = isha.toIsoString(),
    isHanafiMode = isHanafiMode
)