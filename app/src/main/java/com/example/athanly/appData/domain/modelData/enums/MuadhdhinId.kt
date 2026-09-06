package com.example.athanly.appData.domain.modelData.enums

// Stable identity for each Muadhdhin — used to persist the user's selection
// (e.g. in AppDataPreferencesDataStore) without depending on resource ids or list order.
enum class MuadhdhinId {
    MAKKAH,
    MADINAH,
    JERUSALEM,
    ISTANBUL,
    CAIRO,
}