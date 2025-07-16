package com.jolabs.di

import com.jolabs.data.repository.HabitRepository
import com.jolabs.domain.CreateHabitUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideCreateHabitUseCase(habitRepository: HabitRepository) : CreateHabitUseCase {
            return  CreateHabitUseCase(habitRepository)
    }
}