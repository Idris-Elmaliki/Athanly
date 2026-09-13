package com.example.athanly.qibla.local.domain.modelData

import com.example.athanly.qibla.local.domain.modelData.enums.CardinalDirection
import com.example.athanly.qibla.local.domain.modelData.enums.DistanceUnit

// needleRotationDegrees and qiblaDegrees/qiblaDirection are deliberately independent.
// needleRotationDegrees is a live compass hand driven purely by device heading — it
// updates continuously and has no relationship to the Kaaba's direction.
// qiblaDegrees/qiblaDirection are a static snapshot from the last direction calculation
// (local math for now, the API's `direction` field later) — they drive both the Kaaba
// marker's fixed angle on the compass and the "65° NE" text, and only change when an
// explicit recalculation happens, never in response to heading updates.
data class QiblaUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val needleRotationDegrees: Float = 0f,
    val distanceValue: Double = 0.0,
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val qiblaDegrees: Double = 0.0,
    val qiblaDirection: CardinalDirection = CardinalDirection.N
)