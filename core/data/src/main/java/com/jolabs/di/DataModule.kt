package com.jolabs.di

import com.jolabs.data.repository.HabitRepository
import com.jolabs.data.repository.HabitRepositoryImpl
import com.jolabs.database.dao.HabitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideHabitRepository(habitDao: HabitDao) : HabitRepository {
        return HabitRepositoryImpl(habitDao)
    }
}