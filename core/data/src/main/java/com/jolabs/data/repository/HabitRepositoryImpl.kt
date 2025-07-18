package com.jolabs.data.repository

import com.jolabs.data.mapper.toDomain
import com.jolabs.data.mapper.toEntity
import com.jolabs.database.dao.HabitDao
import com.jolabs.database.entity.RepeatTable
import com.jolabs.database.entity.StreakTable
import com.jolabs.database.relation.HabitWithDetails
import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {
    override suspend fun createHabit(habit: CreateHabit) {
        val habitId = habitDao.addHabit(habit.toEntity())

        habitDao.addHabitStreak(StreakTable(
            habitId = habitId,
            currentStreak = 0,
            longestStreak = 0
        ))

        habit.daysOfWeek.forEach { dayOfWeek ->
            habitDao.addHabitRepetition(RepeatTable(
                habitId = habitId,
                dayOfWeek = dayOfWeek,
                timeOfDay = habit.timeOfDay
            ))
        }
    }

    override fun getAllHabits() : Flow<List<HabitBasic>> {
       return habitDao.getAllHabits().map { entities ->
           entities.map(HabitWithDetails::toDomain)
       }
    }

}