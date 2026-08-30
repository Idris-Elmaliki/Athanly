package com.example.athanly.athanTime.local.data.repository

import com.example.athanly.athanTime.local.data.dao.AthanTimeDao
import com.example.athanly.athanTime.local.data.entity.CityEntity
import com.example.athanly.athanTime.local.data.entity.PrayerScheduleEntity
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.local.domain.modelData.PrayerSchedule
import com.example.athanly.athanTime.local.domain.repository.AthanTimeRepository
import com.example.athanly.userInfoCalculations.toIsoString
import com.example.athanly.userInfoCalculations.toLocalDate
import com.example.athanly.userInfoCalculations.toLocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomAthanTimeRepository @Inject constructor(
    private val dao: AthanTimeDao
) : AthanTimeRepository {

    override fun observeCities(): Flow<List<City>> {
        return dao.observeCities().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeSchedule(cityId: String): Flow<PrayerSchedule?> {
        return dao.observeScheduleForCity(cityId).map { it?.toDomain() }
    }

    override suspend fun saveCityWithSchedule(city: City, schedule: PrayerSchedule) {
        dao.upsertCityWithSchedule(city.toEntity(), schedule.toEntity(cityId = city.id))
    }

    override suspend fun deleteCity(cityId: String) {
        dao.deleteCity(cityId)
    }
}

private fun CityEntity.toDomain() = City(
    id = id,
    name = name,
    hijriDate = hijriDate,
    latitude = latitude,
    longitude = longitude
)

private fun City.toEntity() = CityEntity(
    id = id,
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
    isha = isha.toLocalTime()
)

private fun PrayerSchedule.toEntity(cityId: String) = PrayerScheduleEntity(
    cityId = cityId,
    date = date.toIsoString(),
    fajr = fajr.toIsoString(),
    sunrise = sunrise.toIsoString(),
    dhuhr = dhuhr.toIsoString(),
    asr = asr.toIsoString(),
    maghrib = maghrib.toIsoString(),
    isha = isha.toIsoString()
)