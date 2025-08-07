package com.jolabs.data.mapper

import com.jolabs.database.entity.HabitEntryStatus
import com.jolabs.database.entity.HabitEntryTable
import com.jolabs.database.entity.HabitTable
import com.jolabs.database.relation.HabitWithDetails
import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitEntryModel
import com.jolabs.model.HabitStatus

fun CreateHabit.toHabitTableEntity() : HabitTable {
    return HabitTable(
        id= id,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun HabitWithDetails.toDomain() : HabitBasic {
    return  HabitBasic(
        id = habit.id,
        name = habit.name,
        description = habit.description,
        longestStreak = habitStreak.longestStreak.toString(),
        currentStreak = habitStreak.currentStreak.toString(),
        habitState = habitEntry?.isCompleted?.toHabitStatus() ?: HabitStatus.NONE
    )
}

fun HabitWithDetails.toCreateHabit() : CreateHabit {
    return CreateHabit(
        id = habit.id,
        name = habit.name,
        description = habit.description,
        daysOfWeek = habitRepeat.map { it.dayOfWeek },
        timeOfDay = habitRepeat.first().timeOfDay,
        createdAt = habit.createdAt,
        updatedAt = habit.updatedAt
    )
}

fun HabitEntryStatus.toHabitStatus(): HabitStatus {
    return when (this) {
        HabitEntryStatus.NONE -> HabitStatus.NONE
        HabitEntryStatus.COMPLETED -> HabitStatus.COMPLETED
        HabitEntryStatus.SKIPPED -> HabitStatus.SKIPPED
    }
}

fun HabitStatus.toEntity(): HabitEntryStatus {
    return when (this) {
        HabitStatus.NONE -> HabitEntryStatus.NONE
        HabitStatus.COMPLETED -> HabitEntryStatus.COMPLETED
        HabitStatus.SKIPPED -> HabitEntryStatus.SKIPPED
    }
}

fun HabitEntryModel.toEntity() : HabitEntryTable {
    return  HabitEntryTable(
        habitId = habitId,
        date = date,
        isCompleted = isCompleted.toEntity(),
        updatedAtMillis = System.currentTimeMillis()
    )
}