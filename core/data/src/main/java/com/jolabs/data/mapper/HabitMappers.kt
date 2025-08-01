package com.jolabs.data.mapper

import com.jolabs.database.entity.HabitEntryStatus
import com.jolabs.database.entity.HabitEntryTable
import com.jolabs.database.entity.HabitTable
import com.jolabs.database.relation.HabitWithDetails
import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitEntryModel
import com.jolabs.model.HabitStatus

//// TODO need to work on this one
//fun HabitTable.toDomain() : CreateHabit {
//    return CreateHabit(
//        name = name,
//        description = description,
//        daysOfWeek = emptyList(),
//        timeOfDay = t,
//        createdAt = createdAt
//    )
//}

fun CreateHabit.toEntity() : HabitTable {
    return HabitTable(
        name = name,
        description = description,
        createdAt = createdAt
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