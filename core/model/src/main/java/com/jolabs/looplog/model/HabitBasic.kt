package com.jolabs.looplog.model

data class HabitBasic(
    val id: Long,
    val name: String,
    val description: String,
    val longestStreak:String,
    val currentStreak: String,
    val habitState: HabitStatus
)

enum class HabitStatus {
    NONE,COMPLETED,SKIPPED
}