package com.example.athanly.qibla.data

import com.example.athanly.qibla.domain.local.modelData.Coordinates
import com.example.athanly.qibla.domain.local.repository.UserLocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// Fixed New York coordinate, standing in for real GPS until location permissions/City
// persistence are built. QiblaViewModel only depends on the UserLocationRepository
// interface, so swapping this for a real GPS-backed implementation later is a one-line
// change in QiblaModule — nothing else in the feature needs to change.
class MockUserLocationRepository : UserLocationRepository {
    override fun observedLocation(): Flow<Coordinates> {
        return flowOf(
            Coordinates(
                latitude = 40.7128,
                longitude = -74.0060
            )
        )
    }
}