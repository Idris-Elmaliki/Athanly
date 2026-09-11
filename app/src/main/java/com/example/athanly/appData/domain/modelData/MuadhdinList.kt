package com.example.athanly.appData.domain.modelData

import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId

// All selectable Muadhdhin options, in display order
val muadhhinList = listOf(
    Muadhdhin(
        id = MuadhdhinId.MAKKAH,
        locationNameRes = R.string.muadhdhin_makkah,
        sheikhNameRes = R.string.sheikh_farooq_hadhrawi,
        imageRes = R.drawable.mecca,
        fajrAudioRes = R.raw.mecca_fajr_azan_farooq_al_hadrawi,
        otherAudioRes = R.raw.mecca_azan_farooq_al_hadrawi,
    ),
    Muadhdhin(
        id = MuadhdhinId.MADINAH,
        locationNameRes = R.string.muadhdhin_madinah,
        sheikhNameRes = R.string.muhammad_marwan_qassas,
        imageRes = R.drawable.medina,
        fajrAudioRes = R.raw.medina_fajr_azan_muhammad_marwan_qassas,
        otherAudioRes = R.raw.medina_azan_muhammad_marwan_qassas,
    ),
    Muadhdhin(
        id = MuadhdhinId.JERUSALEM,
        locationNameRes = R.string.muadhdhin_jerusalem,
        sheikhNameRes = R.string.sheikh_firas_qazzaz,
        imageRes = R.drawable.jerusalem,
        // Fajr's reciter (Naaji Qazzaz) differs from the other 4 prayers' (Firas Al-Qazzaz) —
        // no single Al-Aqsa reciter had both recordings available
        fajrAudioRes = R.raw.al_aqsa_fajr_azan_naaji_qazzaz,
        otherAudioRes = R.raw.al_aqsa_azan_firas_al_qazzaz,
    ),
    Muadhdhin(
        id = MuadhdhinId.ISTANBUL,
        locationNameRes = R.string.muadhdhin_istanbul,
        sheikhNameRes = R.string.müezzin_ibrahim,
        imageRes = R.drawable.istanbul,
        // Same reasoning as Al-Aqsa above — Fajr's reciter (Ridvan Akbas) differs from the
        // other 4 prayers' (Muezzin Ibrahim)
        fajrAudioRes = R.raw.istanbul_fajr_azan_ridvan_akbas,
        otherAudioRes = R.raw.istanbul_azan_muezzin_ibrahim,
    ),
    Muadhdhin(
        id = MuadhdhinId.CAIRO,
        locationNameRes = R.string.muadhdhin_cairo,
        sheikhNameRes = R.string.imam_muhammad_fawzi_al_barbari,
        imageRes = R.drawable.cairo,
        fajrAudioRes = R.raw.cairo_fajr_azan_muhammad_fawzi_al_barbari,
        otherAudioRes = R.raw.cairo_azan_muhammad_fawzi_al_barbari,
    ),
)