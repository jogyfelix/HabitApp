package com.jolabs.looplog.di

import com.jolabs.looplog.data.repository.HabitRepository
import com.jolabs.looplog.data.repository.HabitRepositoryImpl
import com.jolabs.looplog.database.dao.HabitDao
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