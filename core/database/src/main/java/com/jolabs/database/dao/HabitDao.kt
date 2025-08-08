package com.jolabs.database.dao

import androidx.room.Dao
import androidx.room.Delete
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
    @Upsert
    suspend fun addHabit(habit: HabitTable): Long

    @Query("DELETE FROM HabitTable WHERE id = :habitId")
    suspend fun deleteHabit(habitId : Long): Int

    @Upsert
    suspend fun upsertHabitEntry(habitEntry: HabitEntryTable): Long

    @Upsert
    suspend fun addHabitRepetition(habitRepeat: RepeatTable): Long

    @Upsert
    suspend fun upsertHabitStreak(habitStreak: StreakTable): Long

    @Transaction
    suspend fun upsertHabitWithDetails(habitTable: HabitTable, daysOfWeek: List<DayOfWeek>,timeOfDay: Long?) {
        val habitId =addHabit(habitTable)
        val id = if(habitTable.id == 0L) habitId else habitTable.id
        upsertHabitStreak(
            StreakTable(
                habitId = id,
                currentStreak = 0,
                longestStreak = 0,
            )
        )
        daysOfWeek.forEach { dayOfWeek ->
            addHabitRepetition(
                RepeatTable(
                    habitId = id,
                    dayOfWeek = dayOfWeek,
                    timeOfDay = timeOfDay
                )
            )
        }
    }

    @Transaction
    @Query("SELECT * FROM HabitTable WHERE id=:habitId")
    suspend fun getHabitById(habitId: Long): HabitWithDetails?

    @Query("SELECT * FROM HabitEntryTable WHERE habitId=:habitId")
    suspend fun getHabitEntryById(habitId: Long): List<HabitEntryTable>

    @Query("SELECT * FROM StreakTable WHERE habitId=:habitId")
    suspend fun getHabitStreakById(habitId: Long): StreakTable

    @Query("SELECT * FROM RepeatTable WHERE habitId=:habitId")
    suspend fun getHabitRepetitionById(habitId: Long): List<RepeatTable>

    @Query("SELECT * FROM HabitEntryTable WHERE habitId=:habitId ORDER BY date DESC LIMIT 1")
    suspend fun getLastHabitEntryById(habitId: Long): HabitEntryTable?

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