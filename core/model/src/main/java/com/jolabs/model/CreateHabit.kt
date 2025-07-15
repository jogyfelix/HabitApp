package com.jolabs.model

import java.time.DayOfWeek

data class CreateHabit(
    val name: String,
    val description: String,
    val daysOfWeek: List<DayOfWeek>
)
