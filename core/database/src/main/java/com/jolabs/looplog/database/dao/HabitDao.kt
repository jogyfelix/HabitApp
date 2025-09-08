package com.jolabs.looplog.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.jolabs.looplog.database.entity.HabitEntryTable
import com.jolabs.looplog.database.entity.HabitTable
import com.jolabs.looplog.database.entity.RepeatTable
import com.jolabs.looplog.database.entity.StreakTable
import com.jolabs.looplog.database.relation.HabitWithDetails

import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

@Dao
interface HabitDao {
    @Upsert
    suspend fun addHabit(habit: HabitTable): Long

    @Query("DELETE FROM HabitTable WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long): Int

    @Query("DELETE FROM HabitEntryTable WHERE habitId = :habitId AND date = :date")
    suspend fun deleteHabitEntry(habitId: Long, date: Long): Int

    @Upsert
    suspend fun upsertHabitEntry(habitEntry: HabitEntryTable): Long

    @Upsert
    suspend fun addHabitRepetition(habitRepeat: RepeatTable): Long

    @Upsert
    suspend fun upsertHabitStreak(habitStreak: StreakTable): Long

    @Query("DELETE FROM RepeatTable WHERE habitId = :habitId AND dayOfWeek NOT IN (:newDays)")
    suspend fun deleteRemovedDays(habitId: Long, newDays: List<DayOfWeek>)

    @Transaction
    suspend fun upsertHabitWithDetails(
        habitTable: HabitTable,
        daysOfWeek: List<DayOfWeek>,
        timeOfDay: Long?
    ) : Long {
        val habitId = addHabit(habitTable)
        if (habitTable.id == 0L) {
            upsertHabitStreak(
                StreakTable(
                    habitId = habitId,
                    currentStreak = 0,
                    longestStreak = 0,
                )
            )

            daysOfWeek.map {
                addHabitRepetition(
                    RepeatTable(
                        habitId = habitId,
                        dayOfWeek = it,
                        timeOfDay = timeOfDay
                    )
                )
            }
        } else {
            val existingDays = getHabitRepetitionById(habitTable.id)
                .map { it.dayOfWeek }

            if (existingDays != daysOfWeek) {
                deleteRemovedDays(habitTable.id, daysOfWeek)

                val daysToInsert = daysOfWeek.filter { it !in existingDays }
                daysToInsert.forEach { day ->
                    addHabitRepetition(
                        RepeatTable(
                            habitId = habitTable.id,
                            dayOfWeek = day,
                            timeOfDay = timeOfDay
                        )
                    )
                }
            }

           if(timeOfDay !== null){
               val daysToUpdate = daysOfWeek.filter { it in existingDays }
               daysToUpdate.forEach { day ->
                   updateHabitRepetitionTime(habitTable.id, day, timeOfDay)
               }
           }
        }
        return habitId
    }

    @Query("UPDATE RepeatTable SET timeOfDay = :newTime WHERE habitId = :habitId AND dayOfWeek = :day")
    suspend fun updateHabitRepetitionTime(habitId: Long, day: DayOfWeek, newTime: Long)


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
    @Query("SELECT * FROM HabitTable")
    fun getAllHabitsDirect(): List<HabitWithDetails>


    @Transaction
    @Query("SELECT * FROM HabitTable h INNER JOIN RepeatTable r ON h.id = r.habitId LEFT JOIN HabitEntryTable e ON h.id = e.habitId AND e.date = :selectedDate LEFT JOIN StreakTable s ON h.id = s.habitId WHERE r.dayOfWeek = :dayOfWeek ORDER BY r.timeOfDay ASC")
    fun getHabitByDate(
        dayOfWeek: DayOfWeek,
        selectedDate: Long
    ): Flow<List<HabitWithDetails>>

    @Transaction
    @Query("""
  SELECT * FROM HabitTable h 
  INNER JOIN RepeatTable r ON h.id = r.habitId 
  LEFT JOIN HabitEntryTable e ON h.id = e.habitId AND e.date = :selectedDate 
  LEFT JOIN StreakTable s ON h.id = s.habitId 
  WHERE r.dayOfWeek = :dayOfWeek
  ORDER BY r.timeOfDay ASC
""")
    suspend fun getHabitByDateDirect(
        dayOfWeek: DayOfWeek,
        selectedDate: Long
    ): List<HabitWithDetails>
}

