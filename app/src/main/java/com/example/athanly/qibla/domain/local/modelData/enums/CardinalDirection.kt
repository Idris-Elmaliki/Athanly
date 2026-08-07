package com.example.athanly.qibla.domain.local.modelData.enums

import com.example.athanly.R

// 8-point compass abbreviation shown next to the bearing-to-Kaaba text (e.g. "65° NE").
enum class CardinalDirection(
    val abbreviationRes: Int
) {
    N(
        abbreviationRes = R.string.qibla_cardinal_n
    ),
    NE(
        abbreviationRes = R.string.qibla_cardinal_ne
    ),
    E(
        abbreviationRes = R.string.qibla_cardinal_e
    ),
    SE(
        abbreviationRes = R.string.qibla_cardinal_se
    ),
    S(
        abbreviationRes = R.string.qibla_cardinal_s
    ),
    SW(
        abbreviationRes = R.string.qibla_cardinal_sw
    ),
    W(
        abbreviationRes = R.string.qibla_cardinal_w
    ),
    NW(
        abbreviationRes = R.string.qibla_cardinal_nw
    )
}