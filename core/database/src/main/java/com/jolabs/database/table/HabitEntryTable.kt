package com.jolabs.database.table

import androidx.room.Entity

@Entity(primaryKeys = ["habitId", "date"])
data class HabitEntryTable(
    val habitId: Long,
    val date: Long,
    val isCompleted: HabitEntryStatus = HabitEntryStatus.NONE,
)

enum class HabitEntryStatus {
    NONE,COMPLETED,NOT_COMPLETED
}
