package com.jolabs.data.repository

import com.jolabs.data.mapper.toDomain
import com.jolabs.data.mapper.toEntity
import com.jolabs.database.dao.HabitDao
import com.jolabs.database.entity.HabitEntryStatus
import com.jolabs.database.entity.HabitEntryTable
import com.jolabs.database.entity.RepeatTable
import com.jolabs.database.entity.StreakTable
import com.jolabs.database.relation.HabitWithDetails
import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitEntryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    //TODO make this atomic
    override suspend fun createHabit(habit: CreateHabit) {
        val habitId = habitDao.addHabit(habit.toEntity())

        habitDao.upsertHabitStreak(
            StreakTable(
                habitId = habitId,
                currentStreak = 0,
                longestStreak = 0,
            )
        )

        habit.daysOfWeek.forEach { dayOfWeek ->
            habitDao.addHabitRepetition(
                RepeatTable(
                    habitId = habitId,
                    dayOfWeek = dayOfWeek,
                    timeOfDay = habit.timeOfDay
                )
            )
        }
    }

    override fun getAllHabits(): Flow<List<HabitBasic>> {
        return habitDao.getAllHabits().map { entities ->
            entities.map(HabitWithDetails::toDomain)
        }
    }

    override fun getHabitByDate(dayOfWeek: DayOfWeek, epochDate: Long): Flow<List<HabitBasic>> {
        return habitDao.getHabitByDate(dayOfWeek, epochDate).map { entities ->
            entities.map(HabitWithDetails::toDomain)
        }
    }

    override suspend fun upsertHabitEntry(habitEntry: HabitEntryModel) {
        val habitEntry = habitEntry.toEntity()
        habitDao.upsertHabitEntry(habitEntry)
//        recalculateStreak(
//            habitId = habitEntry.habitId,
//            updatedDate = habitEntry.date,
////            getEntries = { habitDao.getHabitEntryListById(it)},
//            saveStreak = {habitStreak -> habitDao.upsertHabitStreak(habitStreak)}
//        )
    }

    suspend fun recalculateStreak(
        habitId: Long,
        updatedDate: Long,
//        getEntries: suspend (Long) -> List<HabitEntryTable>,
        saveStreak: suspend (StreakTable) -> Unit
    ) {
//        val entries = getEntries(habitId).sortedBy { it.date }
//
//        var currentStreak = 0
//        var longestStreak = 0
//
//        var tempStreak = 0
//        val currentDate = LocalDate.now().toEpochDay()
//
//        // Used to track the streak that ends on today or latest date
//        var potentialCurrentStreak = 0
//        var lastCompletedDate: Long? = null
//
//        for ((i, entry) in entries.withIndex()) {
//            if (entry.isCompleted == HabitEntryStatus.COMPLETED) {
//                if (lastCompletedDate == null || entry.date == lastCompletedDate + 1) {
//                    tempStreak++
//                } else {
//                    // Not consecutive
//                    tempStreak = 1
//                }
//
//                // Update tracking
//                lastCompletedDate = entry.date
//
//                // If streak is ending on or closest before today, consider it current
//                if (entry.date <= currentDate) {
//                    potentialCurrentStreak = tempStreak
//                }
//
//                longestStreak = maxOf(longestStreak, tempStreak)
//            } else {
//                tempStreak = 0
//            }
//        }
//
//        currentStreak = potentialCurrentStreak
        val habit = habitDao.getHabitEntryById(habitId, updatedDate)
        val habitStreak = habitDao.getHabitStreakById(habitId)
       if((habit.isCompleted ?: HabitEntryStatus.NONE) == HabitEntryStatus.COMPLETED)
       {
           val streak = StreakTable(
               habitId = habitId,
               currentStreak = habitStreak.currentStreak + 1,
               longestStreak =  habitStreak.longestStreak + 1
           )
           saveStreak(streak)
       }

    }



}