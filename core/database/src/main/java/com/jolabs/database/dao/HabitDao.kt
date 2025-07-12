package com.jolabs.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.jolabs.database.table.HabitTable

@Dao
interface HabitDao {
 @Insert
 suspend fun addHabit(habit: HabitTable) : Long
}