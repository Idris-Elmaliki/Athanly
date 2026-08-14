package com.example.athanly.athanSelection.domain.modelData.enums

// Stable identity for each Muadhdhin — used to persist the user's selection
// (e.g. in SettingsPreferencesDataStore) without depending on resource ids or list order.
enum class MuadhdhinId {
    MAKKAH,
    MADINAH,
    AL_AQSA,
    KUWAIT,
    SOFT_GENTLE,
}