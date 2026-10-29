package com.example.athanly.ui.home.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.local.domain.modelData.HomeUiState
import com.example.athanly.athanTime.local.domain.modelData.NextPrayerInfo
import com.example.athanly.athanTime.domain.PrayerSchedule
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import com.example.athanly.athanTime.local.domain.repository.AthanTimeRepository
import com.example.athanly.athanTime.domain.RefreshPrayerScheduleUseCase
import com.example.athanly.athanTime.domain.timeFor
import com.example.athanly.location.domain.modelData.LocationUiState
import com.example.athanly.location.domain.repository.LocationProvider
import com.example.athanly.athanNotifications.worker.PRAYER_SYNC_WORK_NAME
import com.example.athanly.athanNotifications.worker.PrayerScheduleSyncWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val clock: Clock,
    private val athanTimeRepository: AthanTimeRepository,
    private val refreshPrayerScheduleUseCase: RefreshPrayerScheduleUseCase,
    private val preferencesDataStore: AppDataPreferencesDataStore,
    private val locationProvider: LocationProvider,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    // Real city/schedule stream in from the local repository's Flows below — both start
    // null until the Location onboarding step saves a city and a schedule gets fetched
    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    init {
        // Mirrors the local repository's city/schedule straight into homeState — any DB
        // write (including a future fetch-and-cache) shows up here automatically since
        // it's all driven by Room's own Flow
        viewModelScope.launch {
            combine(
                athanTimeRepository.observeCity(),
                athanTimeRepository.observeSchedule(),
                preferencesDataStore.observedIs24HourFormat()
            ) { city, schedule, is24HourFormat -> Triple(city, schedule, is24HourFormat) }
                .collect { (city, schedule, is24HourFormat) ->
                    _homeState.update { current ->
                        current.copy(
                            city = city,
                            schedule = schedule,
                            isInitializing = false,
                            is24HourFormat = is24HourFormat
                        )
                    }
                }
        }

        // Fetches-and-caches a fresh schedule whenever the local DB is missing one or holds
        // a stale (not-today) one for the saved city. Runs on Dispatchers.IO since it does
        // network + DB work. Re-runs whenever the city OR Hanafi mode changes — a Hanafi
        // flip changes Asr, so it's simplest to just re-check rather than track which
        // school the cached schedule was computed with.
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                athanTimeRepository.observeCity(),
                preferencesDataStore.observedIsHanafiMode(),
                ::Pair
            ).collect { (city, isHanafiMode) ->
                if (city != null) {
                    refreshIfStale(
                        city = city,
                        today = LocalDate.now(clock),
                        isHanafiMode = isHanafiMode
                    )
                }
            }
        }

        // Recomputes nextPrayerInfo once immediately, then every second, so the Sacred
        // Card's countdown stays live without the UI layer needing its own timer
        viewModelScope.launch {
            while (true) {
                tick()
                delay(1000.milliseconds)
            }
        }
    }

    // Delegates the actual "is it stale, and if so fetch+save" logic to
    // RefreshPrayerScheduleUseCase, shared with PrayerScheduleSyncWorker's daily background
    // run. onFetchStarted only fires once the use case has confirmed a real network call is
    // about to happen, so isLoading (and the Home skeleton it drives) never flips true for a
    // warm cache hit — only for an actual fetch.
    private suspend fun refreshIfStale(
        city: City,
        today: LocalDate,
        isHanafiMode: Boolean
    ) {
        try {
            refreshPrayerScheduleUseCase(
                city = city,
                today = today,
                isHanafiMode = isHanafiMode,
                onFetchStarted = {
                    _homeState.update { current -> 
                        current.copy(
                            isLoading = true
                        ) 
                    }
                }
            )

        }
        catch (e: Exception) {
            _homeState.update { current ->
                current.copy(
                    error = e.message
                )
            }
        }
        finally {
            _homeState.update { current ->
                current.copy(
                    isLoading = false
                )
            }
        }
    }

    // Drives the ChangeCity screen (reached from Home's top bar) — same shape/reasoning as
    // OnBoardingViewModel's own locationState, including the same deferred-save pattern: a tap
    // only updates this local state, and the pick is only committed to Room (see
    // onChangeCityBackClick below) once the user actually leaves the screen.
    private val _locationState = MutableStateFlow(LocationUiState())
    val locationState: StateFlow<LocationUiState> = _locationState.asStateFlow()

    init {
        viewModelScope.launch {
            athanTimeRepository.observeCity().collect { city ->
                _locationState.update { current ->
                    current.copy(
                        hasSavedCity = city != null,
                        city = city
                    )
                }
            }
        }
    }

    // Called with the OS permission dialog result on the ChangeCity screen. Granted kicks off
    // the GPS fetch-and-save; denied leaves the PresetCityPicker as the only way to switch.
    fun onLocationPermissionResult(isGranted: Boolean) {
        _locationState.update { current ->
            current.copy(
                isPermissionGranted = isGranted
            )
        }

        if (isGranted) {
            fetchAndSaveCurrentLocation()
        }
    }

    private fun fetchAndSaveCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            _locationState.update { current -> current.copy(isLoading = true) }
            try {
                val city = locationProvider.getCurrentCity()
                if (city != null) {
                    // Pending only — onChangeCityBackClick is what actually commits this to Room
                    _locationState.update { current -> current.copy(city = city) }
                }
                // city == null (no fix obtained despite permission) just leaves the screen as-is —
                // the previously saved city stays in place, same fallback as onboarding's version.
            } catch (e: Exception) {
                _locationState.update { current -> current.copy(error = e.message) }
            } finally {
                _locationState.update { current -> current.copy(isLoading = false) }
            }
        }
    }

    // Called when the user picks a city from PresetCityPicker — just updates the pending
    // selection shown on screen. Nothing is written to Room until onChangeCityBackClick.
    fun onPresetCitySelected(city: City) {
        _locationState.update { current ->
            current.copy(
                city = city
            )
        }
    }

    // Called when the user leaves the ChangeCity screen. Commits the pending pick to Room (and
    // kicks off a fresh schedule fetch) only if it actually differs from what's already saved —
    // homeState.city is already kept in sync with Room by the init block above, so it doubles
    // as "what's currently persisted" here without a second DB read.
    fun onChangeCityBackClick() {
        val pendingCity = _locationState.value.city
        if (pendingCity != null && pendingCity != _homeState.value.city) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    athanTimeRepository.saveCity(pendingCity)
                    enqueueImmediateScheduleSync()
                } catch (e: Exception) {
                    _locationState.update { current -> current.copy(error = e.message) }
                }
            }
        }
    }

    // Both saveCity call sites above land here right after a successful save. REPLACE (not
    // KEEP, which AthanlyApplication's app-start enqueue uses) is deliberate: the user just
    // changed to a real, different city, so the existing chain (scheduled for the OLD city's
    // alarms) needs to be superseded with a real run right away, rather than waiting for its
    // already-scheduled midnight fire.
    private fun enqueueImmediateScheduleSync() {
        WorkManager.getInstance(context).enqueueUniqueWork(
            PRAYER_SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<PrayerScheduleSyncWorker>().build()
        )
    }

    private fun tick() {
        _homeState.update { current ->
            current.copy(
                nextPrayerInfo = current.schedule?.let { schedule ->
                    computeNextPrayerInfo(schedule, LocalTime.now(clock))
                }
            )
        }
    }

    // Which of the 5 windows between real prayers `now` falls in — checked in order, so this
    // is a straightforward sequential comparison rather than a loop over every prayer.
    // remaining/progressFraction are placeholders here, filled in by withRemainingAndProgress.
    private fun computeNextPrayerInfo(schedule: PrayerSchedule, now: LocalTime): NextPrayerInfo {
        val info = when {
            now.isBefore(schedule.fajr) -> {
                NextPrayerInfo(
                    currentPrayer = Prayer.ISHA,
                    nextPrayer = Prayer.FAJR,
                    startsAt = schedule.fajr,
                    remaining = Duration.ZERO,
                    progressFraction = 0f
                )
            }
            now.isBefore(schedule.dhuhr) -> {
                NextPrayerInfo(
                    currentPrayer = Prayer.FAJR,
                    nextPrayer = Prayer.DHUHR,
                    startsAt = schedule.dhuhr,
                    remaining = Duration.ZERO,
                    progressFraction = 0f
                )
            }
            now.isBefore(schedule.asr) -> {
                NextPrayerInfo(
                    currentPrayer = Prayer.DHUHR,
                    nextPrayer = Prayer.ASR,
                    startsAt = schedule.asr,
                    remaining = Duration.ZERO,
                    progressFraction = 0f
                )
            }
            now.isBefore(schedule.maghrib) -> {
                NextPrayerInfo(
                    currentPrayer = Prayer.ASR,
                    nextPrayer = Prayer.MAGHRIB,
                    startsAt = schedule.maghrib,
                    remaining = Duration.ZERO,
                    progressFraction = 0f
                )
            }
            now.isBefore(schedule.isha) -> {
                NextPrayerInfo(
                    currentPrayer = Prayer.MAGHRIB,
                    nextPrayer = Prayer.ISHA,
                    startsAt = schedule.isha,
                    remaining = Duration.ZERO,
                    progressFraction = 0f
                )
            }
            // past Isha — wraps to tomorrow's Fajr
            else -> {
                NextPrayerInfo(
                    currentPrayer = Prayer.ISHA,
                    nextPrayer = Prayer.FAJR,
                    startsAt = schedule.fajr,
                    remaining = Duration.ZERO,
                    progressFraction = 0f
                )
            }
        }

        return withRemainingAndProgress(info, schedule, now)
    }

    // currentPrayer's time is looked up from the schedule rather than threaded through as
    // its own parameter — one less thing for callers of computeNextPrayerInfo to pass around
    private fun withRemainingAndProgress(
        info: NextPrayerInfo,
        schedule: PrayerSchedule,
        now: LocalTime
    ): NextPrayerInfo {
        val previousTime = schedule.timeFor(info.currentPrayer)
        val secondsUntilNext = wrappedSecondsBetween(now, info.startsAt)
        val windowSeconds = wrappedSecondsBetween(previousTime, info.startsAt)
        val elapsedSeconds = windowSeconds - secondsUntilNext

        return info.copy(
            remaining = Duration.ofSeconds(secondsUntilNext),
            progressFraction =
                if (windowSeconds == 0L) 0f
                else elapsedSeconds.toFloat() / windowSeconds
        )
    }

    // Seconds from `from` to `to`, wrapping across midnight when `to` is earlier in the day
    // (e.g. counting toward tomorrow's Fajr from tonight)
    private fun wrappedSecondsBetween(from: LocalTime, to: LocalTime): Long {
        val diffSeconds = to.toSecondOfDay() - from.toSecondOfDay()
        return (if (diffSeconds < 0) diffSeconds + 86_400 else diffSeconds).toLong()
    }
}