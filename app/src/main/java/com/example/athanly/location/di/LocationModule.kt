package com.example.athanly.location.di

import android.content.Context
import com.example.athanly.location.data.LocationProviderImpl
import com.example.athanly.location.domain.repository.LocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// abstract class, not object — @Binds (interface-to-impl) requires an abstract function,
// which an object can't declare, so the FusedLocationProviderClient-providing @Provides
// function moves into a companion object instead. Mirrors AthanDatabaseModule's shape.
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {
    @Binds
    @Singleton
    abstract fun bindLocationProvider(impl: LocationProviderImpl): LocationProvider

    companion object {
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }
    }
}