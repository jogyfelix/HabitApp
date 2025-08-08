package com.jolabs.data.repository

import Resource
import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitEntryModel
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface HabitRepository {
    suspend fun createHabit(habit: CreateHabit)

    suspend fun deleteHabit(habitId: Long) : Int
    fun getAllHabits() : Flow<Resource<List<HabitBasic>>>

    suspend fun getHabitById(id : Long) : CreateHabit?
    fun getHabitByDate(dayOfWeek: DayOfWeek,epochDate : Long) : Flow<Resource<List<HabitBasic>>>

    suspend fun upsertHabitEntry(habitEntry: HabitEntryModel)
}



