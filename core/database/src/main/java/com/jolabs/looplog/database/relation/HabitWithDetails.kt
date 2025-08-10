package com.jolabs.looplog.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.jolabs.looplog.database.entity.HabitEntryTable
import com.jolabs.looplog.database.entity.HabitTable
import com.jolabs.looplog.database.entity.RepeatTable
import com.jolabs.looplog.database.entity.StreakTable

data class HabitWithDetails(
    @Embedded val habit: HabitTable,
    @Embedded val habitEntry: HabitEntryTable?,
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
