package com.jolabs.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.jolabs.database.entity.HabitEntryTable
import com.jolabs.database.entity.HabitTable
import com.jolabs.database.entity.RepeatTable
import com.jolabs.database.entity.StreakTable

data class HabitWithDetails(
    @Embedded val habit: HabitTable,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val habitEntry: HabitEntryTable?,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val habitRepeat: List<RepeatTable>,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val habitStreak: StreakTable
)
