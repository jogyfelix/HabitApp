package com.jolabs.data.repository

import com.jolabs.model.CreateHabit

interface HabitRepository {
    suspend fun createHabit(habit: CreateHabit)
}



