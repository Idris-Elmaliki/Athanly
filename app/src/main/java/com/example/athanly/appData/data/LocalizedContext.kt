package com.example.athanly.appData.data

import android.content.Context
import android.content.res.Configuration
import com.example.athanly.appData.domain.modelData.Language
import java.util.Locale

// Single place that turns the stored Language into a Context whose resources (strings) and
// layout direction match it, so the in-app choice wins over the device language.
fun Context.localizedFor(language: Language): Context {
    val locale = when (language) {
        Language.Arabic -> Locale.forLanguageTag("ar")
        Language.English -> Locale.ENGLISH
    }

    // Copy first so the original Configuration isn't mutated for everyone else
    val configuration = Configuration(resources.configuration).apply {
        setLocale(locale)
        // setLocale alone doesn't flip RTL on all API levels, so set direction explicitly
        setLayoutDirection(locale)
    }

    return createConfigurationContext(configuration)
}