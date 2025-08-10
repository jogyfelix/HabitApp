package com.jolabs.looplog.data.repository

import Resource
import com.jolabs.looplog.model.CreateHabit
import com.jolabs.looplog.model.HabitBasic
import com.jolabs.looplog.model.HabitEntryModel
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



