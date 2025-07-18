package com.jolabs.data.mapper

import com.jolabs.database.entity.HabitTable
import com.jolabs.database.relation.HabitWithDetails
import com.jolabs.model.CreateHabit
import com.jolabs.model.HabitBasic

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
        name = habit.name,
        description = habit.description,
        longestStreak = habitStreak.longestStreak.toString(),
        currentStreak = habitStreak.currentStreak.toString()
    )
}