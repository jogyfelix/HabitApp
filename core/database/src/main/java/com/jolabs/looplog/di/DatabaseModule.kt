package com.jolabs.looplog.di

import android.content.Context
import androidx.room.Room
import com.jolabs.looplog.database.HabitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context : Context) : HabitDatabase {
        return Room.databaseBuilder(
            context,
            HabitDatabase::class.java,
            HabitDatabase.DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(habitDatabase: HabitDatabase) = habitDatabase.habitDao()

}