package com.jolabs.database.table

import androidx.room.Entity

@Entity(
    primaryKeys = ["habitId", "dayOfWeek"]
)
data class RepeatTable(
    val habitId: Long,
    val dayOfWeek: Int,
)
