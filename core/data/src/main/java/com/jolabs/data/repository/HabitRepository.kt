package com.jolabs.data.repository

import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface HabitRepository {
    suspend fun createHabit(habit: CreateHabit)
    fun getAllHabits() : Flow<List<HabitBasic>>
    fun getHabitByDate(dayOfWeek: DayOfWeek,currentDateMillis : Long) : Flow<List<HabitBasic>>
}



