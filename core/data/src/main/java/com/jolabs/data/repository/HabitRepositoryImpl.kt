package com.jolabs.data.repository

import com.jolabs.data.mapper.toEntity
import com.jolabs.database.dao.HabitDao
import com.jolabs.database.entity.RepeatTable
import com.jolabs.database.entity.StreakTable
import com.jolabs.model.CreateHabit
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
}