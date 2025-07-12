package com.jolabs.database.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StreakTable(
    @PrimaryKey
    val habitId : Long,
    val currentStreak : Int,
    val longestStreak: Int,
)
