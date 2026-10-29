
**No Job tracking for snapshots** (2026-10-25). For one-shot/static data (Qibla
location, distance, bearing — the user is stationary), use a plain
`viewModelScope.launch { ... _state.update { } }`. No tracked `Job?` fields, `cancel()`/
relaunch, or Flow subscriptions. **Why:** user rejected a `refreshJob` in the Qibla
`refresh()` rewrite; the old distanceJob/bearingJob assumed a changing location.
**How to apply:** check whether the data can actually change before adding Job tracking
or reactive streams.

**Arabic localization done, applied without AppCompat** (2026-10-28). `values-ar/strings.xml`
is MSA/Fusha (Claude-written, user-reviewed). The stored `Language` (default English, not the
device language) is the single source of truth: `Context.localizedFor(language)` in
`appData/data/LocalizedContext.kt`, provided in `MainActivity` via `LocalResources` /
`LocalConfiguration` / `LocalLayoutDirection` (not `LocalContext`), fed by `AppViewModel.language`.
The notification receiver and `NotificationChannelInitializer.initialize(language)` (collected in
`AthanlyApplication`) use the same helper. **Why:** the user rejected `AppCompatActivity` /
`setApplicationLocales` / `localeConfig` (AppCompat theme parent would put the splash at risk;
no system per-app language wanted). **How to apply:** don't reintroduce AppCompat locale APIs;
format times with the app locale from `LocalConfiguration`, not `Locale.getDefault()`; the
Qibla compass stays pinned LTR. Keep localization to message/RTL correctness — user said "as
long as the message is the same then that's what matters", so skip digit/separator pinning.

**Athan through DND/silent is a planned post-release update, not v1** (2026-10-28). The user
wants it as the first update shipped to Google Play, to practice the update process. **Why:**
the channels in `NotificationChannelInitializer.kt` use `USAGE_NOTIFICATION`, which DND and
ringer silent/vibrate mute (AlarmManager itself is unaffected). **How to apply:** don't
implement before release. Plan: switch channel audio to `USAGE_ALARM`; since a channel's sound
is fixed at creation, give channels new versioned IDs (e.g. `..._v2` in
`prayerNotificationChannelId`) and `deleteNotificationChannel` the old ones; alarm stream has
its own volume (Settings "Volume Control" should mean that); optionally
`ACCESS_NOTIFICATION_POLICY` + `setBypassDnd(true)` and a Settings toggle for it. Retest on a
real device in silent mode and DND, and bump `versionCode`/`versionName`.
