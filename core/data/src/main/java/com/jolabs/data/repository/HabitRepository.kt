package com.jolabs.data.repository

import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    suspend fun createHabit(habit: CreateHabit)
    fun getAllHabits() : Flow<List<HabitBasic>>
}



