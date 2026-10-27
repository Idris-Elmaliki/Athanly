package com.example.athanly.qibla.local.data

import com.example.athanly.location.domain.repository.LocationProvider
import com.example.athanly.qibla.local.domain.modelData.Coordinates
import com.example.athanly.qibla.local.domain.repository.UserLocationRepository
import javax.inject.Inject

// Real GPS-backed UserLocationRepository, replacing MockUserLocationRepository. A thin
// pass-through over LocationProvider's one-shot suspend fetch, since Qibla only needs a
// single fix and the user is stationary.
class GpsUserLocationRepository @Inject constructor(
    private val locationProvider: LocationProvider
) : UserLocationRepository {
    override suspend fun getCurrentLocation(): Coordinates {
        // Throwing here (rather than returning null) is what lets QiblaViewModel's
        // existing try/catch surface a real error to the user.
        return locationProvider.getCurrentCoordinates()
            ?: throw IllegalStateException("No GPS fix available")
    }

    override fun hasLocationPermission(): Boolean {
        return locationProvider.hasLocationPermission()
    }

    override fun isLocationEnabled(): Boolean {
        return locationProvider.isLocationEnabled()
    }
}