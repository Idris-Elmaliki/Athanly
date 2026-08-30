package com.example.athanly.athanTime.remote.di

import com.example.athanly.athanTime.remote.data.api.AlAdhanApiServiceImpl
import com.example.athanly.athanTime.remote.data.repositoryImpl.AthanApiRepositoryImpl
import com.example.athanly.athanTime.remote.domain.api.AlAdhanApiService
import com.example.athanly.athanTime.remote.domain.repository.AthanApiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AthanApiModule {
    @Binds
    @Singleton
    abstract fun bindAthanApiRepository(impl: AthanApiRepositoryImpl): AthanApiRepository

    @Binds
    @Singleton
    abstract fun bindAlAdhanApiService(impl: AlAdhanApiServiceImpl): AlAdhanApiService
}