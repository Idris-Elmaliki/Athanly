package com.example.athanly.ui.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athanly.athanTime.local.domain.modelData.HomeUiState
import com.example.athanly.athanTime.local.domain.modelData.NextPrayerInfo
import com.example.athanly.athanTime.domain.PrayerSchedule
import com.example.athanly.athanTime.local.domain.modelData.enums.Prayer
import com.example.athanly.athanTime.local.domain.modelData.mockCities
import com.example.athanly.athanTime.local.domain.modelData.mockSchedulesByCityId
import com.example.athanly.athanTime.domain.timeFor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val clock: Clock
) : ViewModel() {
    // Seeded from mock data for now — real prayer-time calculation + city persistence
    // is a separate future plan, swapped in here without touching any UI file
    private val _homeState = MutableStateFlow(
        HomeUiState(
            cities = mockCities,
            schedulesByCityId = mockSchedulesByCityId,
        )
    )
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    init {
        // Recomputes nextPrayerInfo once immediately, then every second, so the Sacred
        // Card's countdown stays live without the UI layer needing its own timer
        viewModelScope.launch {
            while (true) {
                tick()
                delay(1000.milliseconds)
            }
        }
    }

    // Called by the HorizontalPager's page-changed callback as the user swipes between cities
    fun onCitySelected(index: Int) {
        _homeState.update { current ->
            current.copy(selectedCityIndex = index)
        }
    }

    private fun tick() {
        _homeState.update { current ->
            val schedule = current.cities.getOrNull(current.selectedCityIndex)
                ?.let { city -> current.schedulesByCityId[city.id] }
            current.copy(nextPrayerInfo = schedule?.let { computeNextPrayerInfo(it, LocalTime.now(clock)) })
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
