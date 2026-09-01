package com.example.athanly.ui.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athanly.appDataPreference.data.AppDataPreferencesDataStore
import com.example.athanly.athanTime.local.domain.modelData.City
import com.example.athanly.athanTime.local.domain.modelData.HomeUiState
import com.example.athanly.athanTime.local.domain.modelData.NextPrayerInfo
import com.example.athanly.athanTime.domain.PrayerSchedule
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import com.example.athanly.athanTime.local.domain.repository.AthanTimeRepository
import com.example.athanly.athanTime.remote.domain.repository.AthanApiRepository
import com.example.athanly.athanTime.domain.timeFor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    private val athanApiRepository: AthanApiRepository,
    private val preferencesDataStore: AppDataPreferencesDataStore
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
                ::Pair
            ).collect { (city, schedule) ->
                _homeState.update { current ->
                    current.copy(city = city, schedule = schedule)
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
                        city,
                        LocalDate.now(clock),
                        isHanafiMode
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

    // Fetches a fresh schedule from the Athan API and saves it locally only when the
    // cached one is missing or dated for a previous day — first() reads just the current
    // cached value off the Flow rather than subscribing to it, since this only needs a
    // one-time check, not an ongoing observation (the render collector above already
    // handles staying subscribed for rendering).
    private suspend fun refreshIfStale(
        city: City,
        today: LocalDate,
        isHanafiMode: Boolean
    ) {
        val cachedSchedule = athanTimeRepository.observeSchedule().first()
        if (cachedSchedule != null && cachedSchedule.date == today) return

        try {
            val fresh = athanApiRepository.fetchPrayerSchedule(
                latitude = city.latitude,
                longitude = city.longitude,
                date = today,
                useHanafiAsr = isHanafiMode
            )
            athanTimeRepository.saveCityWithSchedule(city, fresh)
        } catch (e: Exception) {
            _homeState.update { current -> current.copy(error = e.message) }
        }
    }

    private fun tick() {
        _homeState.update { current ->
            current.copy(nextPrayerInfo = current.schedule?.let { schedule -> computeNextPrayerInfo(schedule, LocalTime.now(clock)) })
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
            progressFraction = if (windowSeconds == 0L) 0f else elapsedSeconds.toFloat() / windowSeconds
        )
    }

    // Seconds from `from` to `to`, wrapping across midnight when `to` is earlier in the day
    // (e.g. counting toward tomorrow's Fajr from tonight)
    private fun wrappedSecondsBetween(from: LocalTime, to: LocalTime): Long {
        val diffSeconds = to.toSecondOfDay() - from.toSecondOfDay()
        return (if (diffSeconds < 0) diffSeconds + 86_400 else diffSeconds).toLong()
    }
}