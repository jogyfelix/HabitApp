package com.jolabs.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.jolabs.database.entity.HabitEntryTable
import com.jolabs.database.entity.HabitTable
import com.jolabs.database.entity.RepeatTable
import com.jolabs.database.entity.StreakTable
import com.jolabs.database.relation.HabitWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
 @Insert
 suspend fun addHabit(habit: HabitTable) : Long

 @Insert
 suspend fun addHabitEntry(habitEntry: HabitEntryTable) : Long

 @Insert
 suspend fun addHabitRepetition(habitRepeat: RepeatTable) : Long

 @Insert
 suspend fun addHabitStreak(habitStreak: StreakTable) : Long

 @Transaction
 @Query("SELECT * FROM HabitTable WHERE id=:habitId")
 suspend fun getHabit(habitId : Long): HabitWithDetails

 @Transaction
 @Query("SELECT * FROM HabitTable")
 fun getAllHabits(): Flow<List<HabitWithDetails>>

}