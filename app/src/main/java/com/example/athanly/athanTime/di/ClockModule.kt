package com.example.athanly.athanTime.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

// Injecting Clock (instead of calling LocalTime.now() directly in the ViewModel) lets tests
// substitute Clock.fixed(...) for deterministic countdown assertions instead of depending on
// real wall-clock time.
@Module
@InstallIn(SingletonComponent::class)
object ClockModule {
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()
}