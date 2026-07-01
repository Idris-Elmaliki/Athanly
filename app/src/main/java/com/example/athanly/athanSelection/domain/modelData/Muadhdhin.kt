package com.example.athanly.athanSelection.domain.modelData

import androidx.annotation.StringRes
import com.example.athanly.R

data class Muadhdhin(
    @StringRes val locationNameRes: Int,
    @StringRes val sheikhNameRes: Int,
    val gradientStart: Long,
    val gradientEnd: Long,
)