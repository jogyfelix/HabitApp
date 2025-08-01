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
//        val (current, longest) = calculateStreak(habitEntry.habitId, habitEntry.date, habitEntry)
        habitDao.upsertHabitEntry(habitEntry)
//        habitDao.upsertHabitStreak(
//            habitStreak = StreakTable(
//                habitId = habitEntry.habitId,
//                currentStreak = current,
//                longestStreak = longest,
//            )
//        )
    }
//
//    private suspend fun calculateStreak(
//        habitId: Long,
//        entryDate: Long,
//        currentHabitEntry: HabitEntryTable
//    ): Pair<Int, Int> {
//        val streak = habitDao.getHabitStreakById(habitId)
//        val repeatDays = habitDao.getHabitRepetitionById(habitId)
//        val lastEntry = habitDao.getLastHabitEntryById(habitId)
//
//
//        var currentStreak = streak.currentStreak
//        var longestStreak = streak.longestStreak
//
//
//        val prevExpectedDate = getPreviousMatchingDate(
//            LocalDate.ofEpochDay(entryDate),
//            repeatDays.map { it.dayOfWeek }
//        ).toEpochDay()
//
//
//        currentStreak = when {
//            lastEntry == null -> 1 // No previous entry — start fresh
//            lastEntry.isCompleted == HabitEntryStatus.COMPLETED && lastEntry.date == prevExpectedDate ->
//                currentStreak + 1 // Continuation of streak
//            currentHabitEntry.isCompleted == HabitEntryStatus.COMPLETED ->
//                1 // Completed, but not a continuation (gap in streak)
//            else ->
//                maxOf(0, currentStreak - 1) // Skipped/None — decrease streak
//        }
//
//
//        longestStreak = maxOf(currentStreak, longestStreak)
//        return currentStreak to longestStreak
//    }
//
//    private fun getPreviousMatchingDate(
//        fromDate: LocalDate,
//        repeatDays: List<DayOfWeek>
//    ): LocalDate {
//        var date = fromDate.minusDays(1)
//        while (true) {
//            if (repeatDays.contains(date.dayOfWeek)) return date
//            date = date.minusDays(1)
//        }
//    }


}