package com.jolabs.data.repository

import com.jolabs.database.entity.HabitEntryTable
import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitEntryModel
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface HabitRepository {
    suspend fun createHabit(habit: CreateHabit)
    fun getAllHabits() : Flow<List<HabitBasic>>
    fun getHabitByDate(dayOfWeek: DayOfWeek,epochDate : Long) : Flow<List<HabitBasic>>

    suspend fun upsertHabitEntry(habitEntry: HabitEntryModel)
}



