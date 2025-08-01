package com.jolabs.model

data class HabitEntryModel(
    val habitId: Long,
    val date: Long,
    val isCompleted: HabitStatus
)
