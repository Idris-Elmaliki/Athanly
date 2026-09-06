package com.example.athanly.appData.domain.modelData

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId

data class Muadhdhin(
    val id: MuadhdhinId,
    @param:StringRes val locationNameRes: Int,
    @param:StringRes val sheikhNameRes: Int,
    @param:DrawableRes val imageRes: Int,
    // Fajr has its own Athan text (the extra "as-salatu khairun minan-nawm" phrase), so it
    // always needs a separate recording — otherAudioRes covers the other 4 daily prayers
    @param:RawRes val fajrAudioRes: Int,
    @param:RawRes val otherAudioRes: Int,
)