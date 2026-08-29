package com.example.athanly.appData.domain.modelData

import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId

// All selectable Muadhdhin options, in display order
val muadhhinList = listOf(
    Muadhdhin(
        id = MuadhdhinId.MAKKAH,
        locationNameRes = R.string.muadhdhin_makkah,
        sheikhNameRes = R.string.sheikh_ali_ahmad_mulla,
        gradientStart = 0xFF2B6954,
        gradientEnd = 0xFF003527,
    ),
    Muadhdhin(
        id = MuadhdhinId.MADINAH,
        locationNameRes = R.string.muadhdhin_madinah,
        sheikhNameRes = R.string.sheikh_essam_bukhari,
        gradientStart = 0xFF1A3A5C,
        gradientEnd = 0xFF0D1F33,
    ),
    Muadhdhin(
        id = MuadhdhinId.AL_AQSA,
        locationNameRes = R.string.muadhdhin_al_aqsa,
        sheikhNameRes = R.string.sheikh_firas_qazzaz,
        gradientStart = 0xFF3A5A8C,
        gradientEnd = 0xFF1E3050,
    ),
    Muadhdhin(
        id = MuadhdhinId.KUWAIT,
        locationNameRes = R.string.muadhdhin_kuwait,
        sheikhNameRes = R.string.mishary_alafasy,
        gradientStart = 0xFF2B6954,
        gradientEnd = 0xFF003527,
    ),
    Muadhdhin(
        id = MuadhdhinId.SOFT_GENTLE,
        locationNameRes = R.string.muadhdhin_soft_gentle,
        sheikhNameRes = R.string.yusuf_islam,
        gradientStart = 0xFFE9C349,
        gradientEnd = 0xFF735C00,
    ),
)