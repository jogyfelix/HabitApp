package com.jolabs.domain

import com.jolabs.data.repository.HabitRepository
import com.jolabs.model.CreateHabit
import javax.inject.Inject

class CreateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke (habit: CreateHabit) {
        habitRepository.createHabit(habit)

    }
}