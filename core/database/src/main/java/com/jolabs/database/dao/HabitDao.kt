package com.jolabs.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.jolabs.database.entity.HabitEntryTable
import com.jolabs.database.entity.HabitTable
import com.jolabs.database.entity.RepeatTable
import com.jolabs.database.entity.StreakTable
import com.jolabs.database.relation.HabitWithDetails
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

@Dao
interface HabitDao {
    @Insert
    suspend fun addHabit(habit: HabitTable): Long

    @Upsert
    suspend fun upsertHabitEntry(habitEntry: HabitEntryTable): Long

    @Insert
    suspend fun addHabitRepetition(habitRepeat: RepeatTable): Long

    @Insert
    suspend fun addHabitStreak(habitStreak: StreakTable): Long

    @Transaction
    @Query("SELECT * FROM HabitTable WHERE id=:habitId")
    suspend fun getHabit(habitId: Long): HabitWithDetails

    @Transaction
    @Query("SELECT * FROM HabitTable")
    fun getAllHabits(): Flow<List<HabitWithDetails>>

    @Transaction
    @Query("SELECT * FROM HabitTable h INNER JOIN RepeatTable r ON h.id = r.habitId LEFT JOIN HabitEntryTable e ON h.id = e.habitId AND e.date = :selectedDate LEFT JOIN StreakTable s ON h.id = s.habitId WHERE r.dayOfWeek = :dayOfWeek")
    fun getHabitByDate(
        dayOfWeek: DayOfWeek,
        selectedDate: Long
    ): Flow<List<HabitWithDetails>>


}