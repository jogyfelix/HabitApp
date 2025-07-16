package com.jolabs.data.mapper

import com.jolabs.database.table.HabitTable
import com.jolabs.model.CreateHabit

// TODO need to work on this one
fun HabitTable.toDomain() : CreateHabit {
    return CreateHabit(
        name = name,
        description = description,
        daysOfWeek = emptyList(),
        createdAt = createdAt
    )
}

fun CreateHabit.toEntity() : HabitTable {
    return HabitTable(
        name = name,
        description = description,
        createdAt = createdAt
    )
}