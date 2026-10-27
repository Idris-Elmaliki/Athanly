
**No Job tracking for snapshots** (2026-10-25). For one-shot/static data (Qibla
location, distance, bearing — the user is stationary), use a plain
`viewModelScope.launch { ... _state.update { } }`. No tracked `Job?` fields, `cancel()`/
relaunch, or Flow subscriptions. **Why:** user rejected a `refreshJob` in the Qibla
`refresh()` rewrite; the old distanceJob/bearingJob assumed a changing location.
**How to apply:** check whether the data can actually change before adding Job tracking
or reactive streams.
