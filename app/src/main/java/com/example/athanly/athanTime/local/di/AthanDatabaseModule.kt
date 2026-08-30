package com.example.athanly.athanTime.local.di

import android.content.Context
import androidx.room.Room
import com.example.athanly.athanTime.local.data.dao.AthanTimeDao
import com.example.athanly.athanTime.local.data.database.AthanDatabase
import com.example.athanly.athanTime.local.data.repository.RoomAthanTimeRepository
import com.example.athanly.athanTime.local.domain.repository.AthanTimeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// abstract class, not object — @Binds (interface-to-impl) requires an abstract function,
// which an object can't declare, so the Room-providing @Provides functions move into a
// companion object instead.
@Module
@InstallIn(SingletonComponent::class)
abstract class AthanDatabaseModule {
    @Binds
    @Singleton
    abstract fun bindAthanTimeRepository(impl: RoomAthanTimeRepository): AthanTimeRepository

    companion object {
        @Provides
        @Singleton
        fun provideAthanDatabase(@ApplicationContext context: Context): AthanDatabase {
            return Room.databaseBuilder(
                        context = context,
                        klass = AthanDatabase::class.java,
                        name = "athan_database"
                    )
                    .build()
        }

        @Provides
        @Singleton
        fun provideAthanTimeDao(database: AthanDatabase): AthanTimeDao {
            return database.athanTimeDao()
        }
    }
}