package com.example.athanly.qibla.remote.di

import com.example.athanly.qibla.remote.data.api.AlAdhanQiblaApiServiceImpl
import com.example.athanly.qibla.remote.data.repositoryImpl.QiblaApiRepositoryImpl
import com.example.athanly.qibla.remote.domain.api.AlAdhanQiblaApiService
import com.example.athanly.qibla.remote.domain.repository.QiblaApiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Mirrors AthanApiModule's pattern. No separate HttpClient here — AlAdhanQiblaApiServiceImpl
// reuses the same singleton HttpClient already provided by athanTime's KtorClientModule.
@Module
@InstallIn(SingletonComponent::class)
abstract class QiblaApiModule {
    @Binds
    @Singleton
    abstract fun bindQiblaApiRepository(impl: QiblaApiRepositoryImpl): QiblaApiRepository

    @Binds
    @Singleton
    abstract fun bindAlAdhanQiblaApiService(impl: AlAdhanQiblaApiServiceImpl): AlAdhanQiblaApiService
}