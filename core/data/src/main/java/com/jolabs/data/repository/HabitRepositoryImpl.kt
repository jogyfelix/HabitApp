package com.jolabs.data.repository

import com.jolabs.data.mapper.toEntity
import com.jolabs.database.dao.HabitDao
import com.jolabs.model.CreateHabit
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {
    override suspend fun createHabit(habit: CreateHabit) {
        val res = habitDao.addHabit(habit.toEntity())
        println("Result $res")
    }
}