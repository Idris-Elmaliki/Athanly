package com.example.athanly.athanSelection.domain.modelData

import androidx.annotation.StringRes
import com.example.athanly.R
import com.example.athanly.athanSelection.domain.modelData.enums.MuadhdhinId

data class Muadhdhin(
    val id: MuadhdhinId,
    @StringRes val locationNameRes: Int,
    @StringRes val sheikhNameRes: Int,
    val gradientStart: Long,
    val gradientEnd: Long,
)