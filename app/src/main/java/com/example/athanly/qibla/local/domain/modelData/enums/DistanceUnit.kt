package com.example.athanly.qibla.local.domain.modelData.enums

import com.example.athanly.R

// The two distance units the distance-to-Kaaba card can display. unitLabelRes is
// the short suffix shown next to the number (e.g. "km"/"mi"), not a full word.
enum class DistanceUnit(
    val unitLabelRes: Int
) {
    KILOMETERS(
        unitLabelRes = R.string.qibla_unit_km
    ),
    MILES(
        unitLabelRes = R.string.qibla_unit_mi
    )
}