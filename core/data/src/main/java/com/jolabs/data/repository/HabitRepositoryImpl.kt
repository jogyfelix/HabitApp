package com.jolabs.data.repository

import Resource
import com.jolabs.data.mapper.toCreateHabit
import com.jolabs.data.mapper.toDomain
import com.jolabs.data.mapper.toEntity
import com.jolabs.data.mapper.toHabitTableEntity
import com.jolabs.database.dao.HabitDao
import com.jolabs.database.entity.HabitEntryStatus
import com.jolabs.database.entity.StreakTable
import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitEntryModel
import com.jolabs.util.DateUtils.toLocalDateFromEpochDays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    override suspend fun createHabit(habit: CreateHabit) {
        habitDao.upsertHabitWithDetails(habit.toHabitTableEntity(), daysOfWeek = habit.daysOfWeek, timeOfDay = habit.timeOfDay)
        if(habit.id > 0L){
        val(currentStreak,longestStreak)=calculateStreak(habit.id)
        habitDao.upsertHabitStreak(StreakTable(habit.id,currentStreak,longestStreak))
    }
    }

    override suspend fun deleteHabit(habitId: Long) : Int {
        return habitDao.deleteHabit(habitId)
    }


    override fun getAllHabits(): Flow<Resource<List<HabitBasic>>> = flow {
        emit(Resource.Loading())
        habitDao.getAllHabits().collect { habitsWithDetails ->
            val habits = habitsWithDetails.map { it.toDomain() }
            emit(Resource.Success(habits))
        }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }



    override suspend fun getHabitById(id: Long): CreateHabit? {
        val habit = habitDao.getHabitById(id)
        return habit?.toCreateHabit()
    }

    override fun getHabitByDate(dayOfWeek: DayOfWeek, epochDate: Long): Flow<Resource<List<HabitBasic>>> = flow {
        emit(Resource.Loading())
         habitDao.getHabitByDate(dayOfWeek, epochDate).collect { habitsWithDetails ->
             val habits = habitsWithDetails.map {  it.toDomain() }
             emit(Resource.Success(habits))
         }
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
    }

    // TODO make this atomic
    override suspend fun upsertHabitEntry(habitEntry: HabitEntryModel) {
        val habitEntry = habitEntry.toEntity()
        habitDao.upsertHabitEntry(habitEntry)
        val(currentStreak,longestStreak)  =calculateStreak(habitEntry.habitId)
        habitDao.upsertHabitStreak(StreakTable(habitEntry.habitId,currentStreak,longestStreak))
    }


    private suspend fun calculateStreak(habitId: Long): Pair<Int, Int> {
        val entries = habitDao.getHabitEntryById(habitId)
            .filter { it.isCompleted == HabitEntryStatus.COMPLETED }

        val repeatDaysSet = habitDao.getHabitRepetitionById(habitId)
            .map { it.dayOfWeek }
            .toSet()

        if (entries.isEmpty()) return 0 to 0

        val validDates = entries
            .map { it.date.toLocalDateFromEpochDays() }
            .sorted()
            .filter { it.dayOfWeek in repeatDaysSet }

        if (validDates.isEmpty()) return 0 to 0

        // -------- Longest streak --------
        var longestStreak = 1
        var tempStreak = 1

        for (i in 1 until validDates.size) {
            if (areConsecutiveValidDays(validDates[i - 1], validDates[i], repeatDaysSet)) {
                tempStreak++
            } else {
                tempStreak = 1
            }
            longestStreak = maxOf(longestStreak, tempStreak)
        }

        // -------- Current streak --------
        var currentStreak = 1
        var lastDate = validDates.last()

        while (true) {
            val prevExpected = getPreviousValidDate(lastDate, repeatDaysSet)
            if (prevExpected != null && validDates.contains(prevExpected)) {
                currentStreak++
                lastDate = prevExpected
            } else {
                break
            }
        }

        val today = LocalDate.now()
        val lastExpected = getPreviousValidDate(today.plusDays(1), repeatDaysSet)
        if (validDates.last() != lastExpected) {
            if (validDates.last() != lastExpected) {
                val prevExpected = lastExpected?.let { getPreviousValidDate(it, repeatDaysSet) }
                if (prevExpected == null || validDates.last() != prevExpected) {
                    currentStreak = 0
                }
            }
        }


        return currentStreak to longestStreak
    }


    private fun areConsecutiveValidDays(prev: LocalDate, next: LocalDate, repeatDays: Set<DayOfWeek>): Boolean {
        var expected = prev
        while (true) {
            expected = expected.plusDays(1)
            if (expected.dayOfWeek in repeatDays) break
        }
        return expected == next
    }

    private fun getPreviousValidDate(fromDate: LocalDate, repeatDays: Set<DayOfWeek>): LocalDate? {
        var prev = fromDate.minusDays(1)
        var counter = 0
        while (prev.dayOfWeek !in repeatDays) {
            prev = prev.minusDays(1)
            counter++
            if (counter > 7) return null // safety guard
        }
        return prev
    }



}