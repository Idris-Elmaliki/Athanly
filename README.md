# Athanly

An Islamic prayer times app for Android. It gives accurate Athan times for your city, plays the call to prayer from a Muadhdhin of your choice, and points you to the Qibla.

## Features

- **Athan times** for the five daily prayers (Fajr, Dhuhr, Asr, Maghrib, Isha) plus sunrise, based on your location or a city you choose, with a live countdown to the next prayer.
- **Hanafi mode** — a toggle that calculates Asr with the Hanafi shadow length (2x) instead of the standard 1x.
- **18° Fajr** — Fajr and Isha use the 18° angle (Aladhan method 18).
- **Muadhdhins** — choose from real Muadhdhins (Makkah, Madinah, Jerusalem, Istanbul, Cairo) and preview their Athan before selecting.
- **Athan notifications** at each prayer time, scheduled with exact alarms and restored after a reboot.
- **Qibla finder** — a compass that uses the device sensors, with the bearing and distance to the Kaaba.
- **Arabic & English** — full UI in both, with right-to-left layout for Arabic. Pick the language on the welcome screen or later in Settings.
- **12h / 24h time**, adaptive layouts for phones, foldables and tablets.

## Tech stack

| Area | Choice |
|------|--------|
| UI | Jetpack Compose, Material 3, adaptive window size classes (Compact / Medium / Expanded) |
| Navigation | Navigation Compose |
| DI | Dagger Hilt |
| Local storage | Room (cached prayer schedules), DataStore (preferences) |
| Networking | Ktor Client (OkHttp engine) + kotlinx.serialization |
| Prayer times | [Aladhan API](https://aladhan.com/prayer-times-api) |
| Location | Google Play Services Location, `Geocoder` |
| Background work | WorkManager + `AlarmManager` |
| Testing | JUnit4, MockK, Turbine, kotlinx-coroutines-test |

Min SDK 28, target SDK 37.

## Project structure

Each feature gets its own module folder, built from up to four layers:

```
featureName/
  data/      # data sources and repository implementations
  di/        # Hilt modules
  domain/    # models, repository and API interfaces
  worker/    # background work and receivers
```

The UI lives in `ui/`, one folder per page. Every screen has a parent composable that delegates to a size-class composable:

```
screenName/
  compact/    ScreenNameUiCompact.kt
  medium/     ScreenNameUiMedium.kt
  expanded/   ScreenNameUiExpanded.kt
  ScreenNameUi.kt
```

Colors come from `ui/theme/Color.kt` through `MaterialTheme.colorScheme`, dimensions from `res/values/dimens.xml`, and all user-facing text from `strings.xml` (`values/` for English, `values-ar/` for Arabic).

## Localization

The language chosen in the app (stored in DataStore, English by default) decides what the UI shows, regardless of the device language. `Context.localizedFor(language)` in `appData/data/LocalizedContext.kt` builds the locale-wrapped context that `MainActivity` provides to Compose, and the notification code uses the same helper. To add a string, add it to both `values/strings.xml` and `values-ar/strings.xml`.

## Building

1. Clone the repo and open it in Android Studio.
2. Let Gradle sync, then run the `app` configuration on a device or emulator (API 28+).

```
./gradlew assembleDebug    # debug build
./gradlew test             # unit tests
```

The app needs location permission for prayer times and the Qibla, and notification permission (Android 13+) for the Athan. Prayer times need an internet connection the first time a city is loaded.

## Development

The app is built one file at a time, test-first for ViewModels, use cases and repositories. `CLAUDE.md` describes the architecture and conventions in detail, and `memory.md` records project decisions.