# Athanly — Everything Claude Has Memorized

This is THE project memory file — it travels with the repo, so new memories are written
here (in the same turn they're saved to Claude's private per-machine store at
`/Users/idriselm/.claude/projects/-Users-idriselm-AndroidStudioProjects-Athanly/memory/`).

---

## Feedback (how to work in this repo)

**Data class placement** — UI state / model data classes always get their own file in a
`modelData/` folder (or `navData/` for navigation-scoped data), never defined inline
inside a ViewModel or composable file.

**Explicit parameter names** — every data class construction / multi-param constructor
call uses named arguments (`param = value`), never positional-only. Considered sloppy
otherwise.

**One arg per line** — once a constructor call or enum entry has 3+ named arguments,
each goes on its own line rather than condensing onto one line.

**No `Color()` in UI** — `Color(...)`/`Color.White`/etc. is only ever called in
`Color.kt`. Composables reference colors exclusively via `MaterialTheme.colorScheme`.
Per-item dynamic colors (e.g. old Muadhdhin gradients) get stored on the data model
itself, built from `Color.kt` constants.

**LazyColumn preference** — any vertical list of row items uses `LazyColumn`, even a
small fixed-size list (e.g. 6 prayer rows). Never `Column` + `.forEach` / `verticalScroll`.

**Prefer existing lambda threading** — when a composable/graph builder needs a new
capability, extend the existing plain `() -> Unit` callback pattern already threaded
through the call chain rather than introducing a new dependency type (raw
`NavController`, a wrapper/actions class) deeper into the stack.

**athanTime module naming** — all athan-related data (City, PrayerSchedule, even
Home-screen UI state derived from them) lives under one top-level `athanTime/` module,
specifically `athanTime/local/domain/modelData/` (`local` before `domain`) — never
`city/`, `prayerTimes/`, or `home/domain/...`.

**Settings sub-screen picker placement** — a screen reached only by navigating *from*
Settings (language picker, Muadhdhin picker) nests flat inside
`settings/settingsSubScreens/<pickerName>/` with no `compact/`/`medium`/`expanded`
subfolders — not a new top-level `ui/home/screens/` sibling with the standard
per-screen layout. That standard layout is reserved for actual bottom-nav destinations.

**API interface in domain/** — for a remote layer's raw API service class, the interface
goes in `domain/api/`, the impl in `data/api/` (mirrors the `domain/repository/` +
`data/repositoryImpl/` split). Impl classes are suffixed `Impl` (`AlAdhanApiServiceImpl`),
never tech-prefixed (`KtorAlAdhanApiService`) — even though the older Room-based
repository (`RoomAthanTimeRepository`) does use a tech-prefix style; that older
convention is not extended to new code.

**Remote layer build order** — build a new remote/API layer bottom-up: DTOs → API
interface/impl → domain repository interface → repositoryImpl → DI wiring. Not
top-down (repository interface first).

**Verify API docs before DTO** — never write DTO/response-model classes for an external
API from memory. Fetch real docs or make a live call (curl/WebFetch) first and confirm
exact field names/nesting.

**TDD step by step** — when CLAUDE.md's TDD rule applies (ViewModels, UseCases,
Repositories, calculation utilities), never dump a full implementation in one `Write`.
Build one function at a time: explain the concept/math, add just enough to pass the
next test, pause for a go-ahead before continuing.

**No compile-check every file** — don't run a gradle compile check after every file
during the one-file-at-a-time review loop, or after every step of a multi-file fix.
Only compile at meaningful checkpoints (end of a phase, suspected break) or when asked.
The user's IDE already shows live compile errors.

**No unprompted gradle test runs** — during a multi-file TDD build, never run
`./gradlew test`/`compileDebugKotlin` proactively — not per increment, and not even
automatically once everything is written. Write everything, stop, let the user decide
when to run the build. This goes further than the compile-check rule above: gradle runs
are the user's call, period.

**IDE rename refactors** — let the user perform class/file/package renames or directory
moves via Android Studio's own refactor tool (Rename/Move), not by manually
editing/creating files and fixing imports. Stop and ask, then wait for confirmation
before continuing dependent work.

**Nav Compose default transitions** (project-flavored, filed as feedback context) — this
app's `NavHost` (Navigation Compose 2.9.x) applies a real default fade transition to
every destination. A screen with its own custom entrance animation may need
`enterTransition = { EnterTransition.None }` on its specific `composable<T>(...)` to
avoid the two animations fighting.

**Sync checks, no dispatcher** — don't wrap a plain synchronous check (e.g.
`ConnectivityManager`-backed `isOnline()`, `ContextCompat.checkSelfPermission`-backed
`hasLocationPermission()`) in `viewModelScope.launch(Dispatchers.IO) { ... }` alongside
a real I/O call. Run the synchronous check + its `_state.update {}` directly (no
coroutine needed), and launch a *separate* coroutine on `Dispatchers.IO` only around
the genuine I/O part. Discovered building Qibla's offline/permission-denied blocker:
putting a synchronous check on `Dispatchers.IO` races it against `init{}`'s other
Main-dispatcher coroutines under `UnconfinedTestDispatcher`, making `StateFlow`
emission order non-deterministic and Turbine `awaitItem()` sequencing flaky.

**Drop generic fallback branches** — when a `when`-based error/blocker UI gets new,
more specific branches (e.g. splitting "offline" out from a generic error), remove the
old generic catch-all branch entirely rather than keeping it as a trailing fallback.
Confirmed while building Qibla's blocker differentiation: the user removed a leftover
`state.error != null -> GenericBlocker(...)` fallback and asked for it to be dropped
from all three screen size classes for consistency — preferring an unhandled case show
nothing over a blocker whose message might not match the actual problem.

**No Job tracking for snapshots** (2026-10-25) — for one-shot/static data (Qibla
location, distance, bearing — the user is stationary), use a plain
`viewModelScope.launch { ... _state.update { } }`. No tracked `Job?` fields,
`cancel()`/relaunch, or Flow subscriptions. **Why:** user rejected a `refreshJob` in the
Qibla `refresh()` rewrite; the old distanceJob/bearingJob assumed a changing location.
**How to apply:** check whether the data can actually change before adding Job tracking
or reactive streams.

**Don't over-engineer presentation** (2026-10-28) — when localizing, the user said "don't
worry about any alteration of data, as long as the message is the same then that's what
matters", and rejected a `DistanceCard.kt` edit that pinned `Locale.ROOT` for digits and
grouping ("WHAT is this for??"). **How to apply:** limit localization changes to text
language, RTL correctness, and things that change the message (notification text, AM/PM
language). Skip digit/separator pinning and similar formatting extras; ask first if one
seems important.

---

## Project context (state, decisions, history)

**Athan API is Aladhan** — `api.aladhan.com`, no key required.
`GET /v1/timings/{date}?latitude={lat}&longitude={lng}&method=18&school={0|1}`.
`method=18` (Tunisia) gives 18°/18° Fajr+Isha, matching the app's strict-Fajr
requirement. `school=0` = standard Asr, `school=1` = Hanafi. Final layout:
`AlAdhanApiService` in `athanTime/remote/domain/api/`, impls suffixed `Impl`,
`PrayerSchedule` lives flat at `athanTime/domain/PrayerSchedule.kt` (shared by local +
remote repositories).

**Roadmap reorder (2026-08-30)** — Athan API + local DB + notifications were moved
ahead of Qibla in the build order (they're interdependent; Qibla is fully independent
and can wait). Order was: (1) Athan API business logic, (2) ViewModel for local DB +
API, (3) WorkManager Alarm & Notification, (4) plug into UI, (5–7) Qibla API + VM + UI,
(8) splash/loading, (9) app logo. **Status: steps 1–4 are complete** (confirmed this
session — Room caching, RefreshPrayerScheduleUseCase, AlarmScheduler, notification
channels/receiver, and Home UI are all wired and working, including the city-change
staleness fix). Step 5+ (Qibla) is the current work.

**Change-city feature** — done. The hamburger icon on `HomeTopBar` is the change-city
entry point (icon swapped to communicate "change location," not a nav drawer). Reuses
onboarding's location-acquisition machinery and `AthanTimeRepository.saveCity()`;
`RefreshPrayerScheduleUseCase` picks up the new city automatically via a
latitude/longitude freshness check added specifically to catch this case (a same-day
cached schedule for the *old* city was previously misread as fresh for the new one).

**Athan audio pipeline** — done. Exactly two sound assets per Muadhdhin (one for Fajr,
one shared for the other four), not five — because a `NotificationChannel`'s sound is
fixed at creation time on API 26+, this required one Fajr channel + one shared channel
*per Muadhdhin* (10 channels total for 5 Muadhdhins), with `PrayerNotificationReceiver`
reading the live-selected Muadhdhin from DataStore at fire-time to pick the right
channel.

**Arabic localization done, applied without AppCompat** (2026-10-28) — replaces the old
"Arabic is the last step" note. `values-ar/strings.xml` is MSA/Fusha (Claude-written,
user-reviewed). The stored `Language` (default English, not the device language) is the
single source of truth: `Context.localizedFor(language)` in
`appData/data/LocalizedContext.kt`, provided in `MainActivity` via `LocalResources` /
`LocalConfiguration` / `LocalLayoutDirection` (not `LocalContext`), fed by
`AppViewModel.language`. The notification receiver and
`NotificationChannelInitializer.initialize(language)` (collected in `AthanlyApplication`)
use the same helper. **Why:** the user rejected `AppCompatActivity` /
`setApplicationLocales` / `localeConfig` (AppCompat theme parent would put the splash at
risk; no system per-app language wanted). **How to apply:** don't reintroduce AppCompat
locale APIs; format times with the app locale from `LocalConfiguration`, not
`Locale.getDefault()`; the Qibla compass stays pinned LTR (N/E/S/W are physical
directions). See also "Don't over-engineer presentation" above.

**Athan through DND/silent is a planned post-release update, not v1** (2026-10-28) — the
user wants it as the first update shipped to Google Play, to practice the update process.
**Why:** the channels in `NotificationChannelInitializer.kt` use `USAGE_NOTIFICATION`,
which DND and ringer silent/vibrate mute (AlarmManager itself is unaffected). **How to
apply:** don't implement before release. Plan: switch channel audio to `USAGE_ALARM`;
since a channel's sound is fixed at creation, give channels new versioned IDs (e.g.
`..._v2` in `prayerNotificationChannelId`) and `deleteNotificationChannel` the old ones;
alarm stream has its own volume (Settings "Volume Control" should mean that); optionally
`ACCESS_NOTIFICATION_POLICY` + `setBypassDnd(true)` and a Settings toggle for it. Retest
on a real device in silent mode and DND, and bump `versionCode`/`versionName`.

**Qibla location differentiation** — done (2026-09-22). The Qibla blocker used to fire
off any generic `QiblaUiState.error`, which conflated permission-denied, GPS-fix
timeout, and unrelated API failures into one misleading message. Now split into two
precise, independently-checked states: `isOnline` (live check via a new `connectivity/`
module, never persisted) and `isLocationPermissionGranted` (persisted in
`AppDataPreferencesDataStore`, rechecked at `QiblaViewModel` init/`retryLocationAccess()`
and written at onboarding grant time), each with its own dedicated blocker UI.
**Known gap, left deliberately unhandled:** online + permission-granted + GPS still
acquiring a fix (weak signal/indoors) shows no UI at all now — the old generic fallback
branch was removed rather than kept (see Drop generic fallback branches above). If this
becomes a real complaint, it needs its own dedicated third state, not a revived generic
blocker — check the `when` blocks in `QiblaUiCompact.kt`/`QiblaUiMedium.kt`/
`QiblaUiExpanded.kt` (kept in sync across all three) for where it would go.

---

## Not yet in the memory store, but true as of this session

(Worth folding into memory files soon — noted here so nothing gets lost.)

- Qibla feature work is now underway: replacing `MockUserLocationRepository` (hardcoded
  NYC) with a real GPS-backed implementation. Explicit user decision: Qibla must force a
  **fresh live GPS fetch every time the user visits the Qibla tab** — no reuse of the
  saved City (too imprecise/stale for exact bearing math) and no local caching of the
  fetched coordinate between visits.
- This is being built via a `LocationProvider.getCurrentCoordinates()` addition (leaner
  than `getCurrentCity()` — skips reverse-geocoding, which Qibla doesn't need), a
  redesigned `UserLocationRepository` (one-shot `suspend fun getCurrentLocation():
  Coordinates?` instead of a continuously observed `Flow`), and a `QiblaViewModel`
  refresh triggered from a `LaunchedEffect(Unit)` in `QiblaUi.kt` — necessary because the
  bottom nav uses `saveState`/`restoreState`, so `QiblaViewModel`'s `init{}` only runs
  once per app session, not once per visit.
**Qibla GPS-pending: hide the icon, no loading screen** (2026-10-22). Rejected a
"Finding your location…" loading indicator for the GPS-fix-pending case. Instead the
Qibla icon on the compass is simply not shown until the coordinates/bearing arrive from
the API call. **Why:** user prefers less UI over a dedicated loading state. **How to
apply:** for pending-data cases on Qibla, hide the dependent element rather than adding
a loading screen/indicator.

**Roadmap step 7 (Qibla VM ↔ UI fixes) — plan + progress as of 2026-10-22.**
Roadmap steps 1–6 are done; 8 (splash/loading animation) and 9 (app logo) not started.
Full plan file: `~/.claude/plans/calm-wiggling-wind.md` (may be superseded by the notes below).
Process reminders: one file at a time, stop for review; TDD with teaching comments for VM
changes; no unprompted gradle runs; one named arg per line.

Problems being fixed: (1) blocker stayed after enabling location because the button opened
the device location toggle while the blocker checks the app permission; (2) retry didn't
reset isLoading/error and bearing job wasn't tracked; (3) compass direction wrong; (4) GPS
pending showed nothing — decision: NO loading screen, hide the Kaaba marker until the API
bearing arrives (qiblaDegrees is now `Double?`, null = not fetched).

DONE (uncompiled/untested — no gradle runs yet):
- `QiblaUiState`: added `isLocationServicesEnabled`; `qiblaDegrees: Double? = null`.
- `isLocationEnabled()` added to `UserLocationRepository`, `GpsUserLocationRepository`,
  `LocationProvider`, `LocationProviderImpl` (LocationManager.isLocationEnabled).
- `QiblaViewModel`: reads isLocationEnabled in `checkLocationAccessPreconditions()`;
  `retryLocationAccess()` sets isLoading=true/error=null; tracked `bearingJob`.
- `QiblaViewModelTest`: stubbed isLocationEnabled in all mocks; new tests for services-off,
  retry reset, null qiblaDegrees; existing assertions use `!!`.
- `QiblaUi.kt`: new `onOpenAppSettingsClick` lambda (ACTION_APPLICATION_DETAILS_SETTINGS),
  passed to all three size classes.
- `QiblaCompass.kt`: `kaabaMarkerDegrees: Float?`, marker drawn only when non-null.
- `QiblaUiCompact/Medium/Expanded`: new param, `?.toFloat()`, `when` = offline →
  permission denied (app settings) → services off (location settings).

REMAINING, in order:
1. `DistanceCard.kt` — accept nullable `qiblaDegrees` (read file first to decide what the
   bearing text shows while null). Build is broken until this is done.
2. Compass direction fix in `QiblaCompass.kt` (+ call sites): dial (N/E/S/W + Kaaba marker at
   qiblaDegrees) rotates by -heading with a fixed top pointer. REVERSES the earlier "match
   heading directly, no negation" decision — confirm with user first. Update VM comment.
3. `LocationOffBlocker.kt` cleanup: hardcoded `Color.Transparent` and `9999.dp` (also in
   `OfflineBlocker.kt` for Color.Transparent) → Color.kt / dimens.xml. Ask user whether
   permission-denied needs its own message/button strings (currently reuses "Location is
   off… Enable Location").
4. Optional: rethrow `CancellationException` in QiblaViewModel's fetch catches (a retry's
   cancel can leave a stale error) — offered, user hasn't answered; needs a test first.
5. Optional: sensor smoothing in `SensorCompassRepository.kt` only if jitter shows on device.
Then verify on device (see plan file), run tests only when user asks.
