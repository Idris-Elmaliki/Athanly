# CLAUDE.md

## App Overview

**Athanly** is an Islamic prayer times app with the following features:

### 1. Athan Times
The core feature — fetches accurate prayer times based on the user's current city/location for the 5 daily prayers: Fajr, Dhuhr, Asr, Maghrib, Isha.

### 2. Hanafi Mode
A toggle for Hanafi madhab users who pray Asr later. The Hanafi calculation uses a shadow length of 2x the object before calculating Asr time, as opposed to the standard 1x (Shafi/Maliki/Hanbali).

### 3. 18 Degree Fajr
Fajr begins when the sun is 18 degrees below the horizon — not the commonly used 15 degrees. This is a stricter/more conservative calculation.

### 4. Wide Variety of Muadhdhins
A selection of real Muadhdhins from around the world for the Athan audio — giving users cultural and personal preference in who they hear the call to prayer from.

### 5. Arabic & English
Full localization support for both Arabic and English languages throughout the entire app UI.

### 6. Qibla Finder
A compass feature that uses the device's sensors to point the user in the direction of the Kaaba (Mecca) from wherever they are in the world.

---

## App Flow

### 1. Splash Screen
The OS-level splash screen is shown on every app boot using the Android `SplashScreen API`, configured via `themes.xml` and `res/drawable`. This is NOT a Compose composable — it is triggered by the Android OS itself during app boot before any Compose content loads.

### 2. First Time User — Onboarding
If it's the user's first time opening the app, they are routed to the onboarding flow which collects:
- Location + location permissions
- Notification permissions
- Muadhdhin choice
- Delayed Asr toggle (Hanafi mode)

The exact order of these steps is TBD. After completing onboarding, the user lands on the Homepage.

### 3. Returning User
On every subsequent app open: Splash Screen → Homepage directly, skipping onboarding.

### 4. Homepage & Navigation
The Homepage displays the Athan times. A bottom navigation bar provides access to 4 tabs in this exact order:
1. Home — Athan times
2. Qibla — Qibla compass finder
3. Settings
4. Profile

---

## Directory Structure

### Top-Level Feature Modules
Each feature gets its own top-level module folder. The underscore prefix on a folder is used purely for alphabetical ordering and carries no architectural meaning.

The `ui/` folder is always the **last** folder in the top-level structure. The only exception is if a `userInfoCalculations/` directory is needed (for any calculation or math manipulation logic used across the app), in which case `ui/` is **second to last**.

---

### Inside a Feature Module
A feature can have up to 4 possible root folders depending on what it needs:

```
featureName/
  data/
  di/
  domain/
  worker/
```

The depth and breakdown of each folder scales with the feature's complexity — simple features stay flat, complex ones subdivide.

**`data/`**
If there is no need to separate between local and remote, files sit flat inside `data/` directly — no `local/` or `remote/` split needed.

If a split is needed, `data/` can contain:
- `local/` — local data sources, with structure depending on the type:
  - **Room** breaks down into:
    - `dao/` — DAO interfaces
    - `database/` — Database class
    - `entity/` — Entity classes
    - `repository/` — Repository implementation
  - **DataStore** stays flat — the DataStoreManager file sits directly inside `data/` with no subdirectories
- `remote/` — remote data sources, which can further contain:
  - `api/` — API interface definitions
  - `di/` — data-layer dependency injection
  - `dto/` — data transfer objects
  - `repositoryImpl/` — repository implementations

**`domain/`** can split into:
- `local/` — local domain concerns
- `remote/` — remote domain concerns

Both `local/` and `remote/` can contain the following if needed:
- `modelData/` — domain model classes (can further contain `enums/` if needed)
- `repository/` — repository interfaces

---

## Window Size Class & Adaptive UI

All UI must adapt to 3 window size classes using `WindowSizeClass`, following the Android standard:

| Breakpoint (dp) | Window Size Class | Columns | Min Margins |
|---|---|---|---|
| 0–599 | Compact | 4 | 8dp |
| 600–839 | Medium | 12 | 12dp |
| 840+ | Expanded | 12 | 32dp |

For every screen, Claude must create 3 separate composables — one per size class:
- `ScreenNameCompact` — Compact (0–599dp), 4 columns, 8dp min margins
- `ScreenNameMedium` — Medium (600–839dp), 12 columns, 12dp min margins
- `ScreenNameExpanded` — Expanded (840+dp), 12 columns, 32dp min margins

And a **parent composable** (`ScreenNameUi.kt`) that receives the `WindowSizeClass` and delegates to the correct size class composable using a `when` block.

Each size class composable must follow the correct columns and minimum margins as per the Android standard table above.

---

### The `ui/` Layer
Page-level folders live here. Each page folder follows this pattern:

```
ui/
  pageName/
    screens/
      screenName/
        compact/
          ScreenNameUiCompact.kt
        medium/
          ScreenNameUiMedium.kt
        expanded/
          ScreenNameUiExpanded.kt
        ScreenNameUi.kt     ← parent composable, delegates to correct size class
    viewModel/              ← viewmodels scoped to this page
    PageLayout.kt           ← top-level page container
```

For ordered multi-step flows, screen folders inside `screens/` use a numbered prefix to enforce ordering: `_1_`, `_2_`, `_3_`, etc.

Shared across all pages:
- `_navigation/` — all navigation concerns (routes, graphs, actions, back handling)
- `subscreens/` — shared reusable UI components (alerts, animations, loading, pagination)
- `theme/` — app theming
- `viewModel/` — app-level viewmodels

---

## Naming Conventions
- **PascalCase** is used for all file and class names
- **Underscore prefix** on a folder → forced alphabetical ordering only, no architectural meaning
- **Numbered prefix** (`_1_`, `_2_`, `_3_`…) → enforced screen order in a multi-step flow
- **Dot notation** in folder names → scoping clarifier (e.g. `data.local`, `splashScreen.domain`, `navData.homepage`)
- `Layout.kt` → top-level container file for a page
- `Ui.kt` suffix → screen-level composable file
- `UiCompact.kt` / `UiMedium.kt` / `UiExpanded.kt` suffix → size class specific composable files
- `ViewModel` suffix → ViewModel class

---

## Resource & Theming Rules

### 1. Colors
Never hardcode a color anywhere. The strict flow is:
- Define the color value in `com/example/athanly/ui/theme/Color.kt`
- Add it to `LightColorScheme` and/or `DarkColorScheme` in `Theme.kt`, referencing the values from `Color.kt`
- Always call colors exclusively through `MaterialTheme.colorScheme` — never directly

### 2. Dimensions (Padding & FontSize)
Never use a hardcoded `.dp` or `.sp` value anywhere. Always:
- Define the dimension in `res/values/dimens.xml`
- Retrieve it using `dimensionResource()`

- **Padding** → always from `dimens.xml` via `dimensionResource()`, no exceptions
- **FontSize** → the base typography style from `MaterialTheme.typography` is always used first, but `.copy()` can be used to override the fontSize. Only pull from `dimens.xml` via `dimensionResource()` if: (1) the exact fontSize value already exists in `dimens.xml`, AND (2) the dimension name is appropriate for a fontSize context — never reuse a padding-named dimension for fontSize. Otherwise, set the fontSize directly in `.copy()`

### 3. Strings
Any text that is either repeated in multiple places OR is considered important must:
- Be defined in `res/values/strings.xml`
- Be retrieved using `stringResource()`

### 4. Typography
Never define a custom text style inline. The strict flow is:
- Define or override the font family style in `com/example/athanly/ui/theme/Type.kt`
- Always call text styles exclusively through `MaterialTheme.typography`
- There must **always** be a base `MaterialTheme.typography` style — `.copy()` can be used on top of that base to make modifications, but never start from scratch inline
- Use the **correct typography type** for the context, following what the Android SDK defines each type as
- The padding around a text container must be appropriate and consistent with the characteristics of that typography type

---

## UI Screen Size Rule
If a screen requires **300+ lines** to fully implement, the subscreen directory lives inside the size class folder:

```
screenName/
  compact/
    screenNameSubScreens/
      SomeSubSection.kt
    ScreenNameUiCompact.kt
  medium/
    screenNameSubScreens/
      SomeSubSection.kt
    ScreenNameUiMedium.kt
  expanded/
    screenNameSubScreens/
      SomeSubSection.kt
    ScreenNameUiExpanded.kt
  ScreenNameUi.kt   ← parent composable
```

- Extract every composable function **except** the main scaffold function into the subscreen directory
- The main scaffold function stays in the size class screen file
- The main scaffold function **calls** the extracted subfunctions from the subscreen directory

---

## Tech Stack

### UI Framework
Jetpack Compose — all UI is written in Compose, no XML layouts

### Local Storage
- **Room** — for structured/relational local data (entities, DAOs, database)
- **DataStore** — for simple key-value or preference-based local storage

### Networking
Ktor Client — handles all RESTful API calls

### Serialization
kotlinx.serialization — used for both:
- Ktor Client JSON parsing/serialization of API responses
- Navigation 3 type-safe route arguments

### Navigation
Navigation 3 with kotlinx.serialization for type-safe route arguments

### Dependency Injection
Dagger Hilt — all DI is done through Hilt

**Module structure:**
- Always annotated with `@Module` and `@InstallIn(SingletonComponent::class)`
- Always an `object`
- Uses `@Provides` and `@Singleton` for providing dependencies
- `@ApplicationContext` used when context is needed
- Dependencies are provided by directly instantiating the implementation inside the `@Provides` function
- Claude has wiggle room to deviate when the situation calls for it (e.g. `@Binds` with `abstract class`, different Hilt component scopes)

**ViewModel injection:**
- All ViewModels are annotated with `@HiltViewModel`
- Page-level composables always receive the ViewModel via `hiltViewModel()` as a default argument:
```kotlin
@Composable
fun ScreenNameUi(
    viewModel: ScreenViewModel = hiltViewModel()
) { }
```
- Sub-composables never call `hiltViewModel()` — they receive the ViewModel or data as parameters passed down from the parent

### Push Notifications
Firebase Cloud Messaging (FCM)

### Image Loading
Coil — for all image loading within Compose

### Coroutines & Flow

**Coroutines:**
All async work uses `viewModelScope.launch(Dispatchers.X)` where `X` is the appropriate dispatcher for the situation:
- `Dispatchers.IO` — network calls, database, file operations
- `Dispatchers.Main` — UI updates
- `Dispatchers.Default` — CPU-intensive work

`try/catch` must be used inside coroutine blocks where failures are possible (network calls, DB operations, etc.):
```kotlin
viewModelScope.launch(Dispatchers.IO) {
    try {
        // operation
    } catch (e: Exception) {
        // handle error
    }
}
```

**Flow:**
Flow is only used when retrieving/observing data (e.g. reading from Room, DataStore, or observing a stream of data from a repository). Not used as a general-purpose async tool.

**State updates:**
All StateFlow updates must use `.update { }` — never reassign directly:
```kotlin
_state.update { data -> data.copy(...) }
```

**UI collection:**
All data objects in the UI must be collected using `collectAsStateWithLifecycle()` — never `collectAsState()`

---

## Testing

### Methodology
Test-Driven Development (TDD) — the test is always written **before** the implementation. The cycle is:
1. Write the test first (it fails — red)
2. Write the minimum implementation to make it pass (green)
3. Refactor if needed (refactor)

This applies to **all** of the following: ViewModels, UseCases, and Repositories.

### Libraries
- **JUnit4** — standard test runner
- **Mockk** — Kotlin-first mocking library for mocking dependencies
- **Turbine** — for testing Flow emissions
- **kotlinx-coroutines-test** — for testing coroutines with `runTest` and `TestDispatchers`
- **Compose UI Testing** — Jetpack's own library for composable/UI testing

### Teaching Rule
Since this is a first-time TDD codebase, every test Claude writes must:
- Explain the testing library syntax being used
- Explain what the test is doing step by step
- Explain why the test is structured that way
- Include comments **inside** the test code itself walking through each part
Claude must also go step by step, he CANNOT just write the code fully (until the go ahead is given)
---

## Code Comments

All code Claude writes must include comments that explain **why** the code is there — not just what it does, but the reasoning and intent behind it. This applies everywhere: ViewModels, UseCases, Repositories, UI composables, DI modules, and tests. AND ENSURE THAT IT IS SHORT!
All additions to **app/build.gradle.kts** **must** have a comment above it stating the name of the package.
