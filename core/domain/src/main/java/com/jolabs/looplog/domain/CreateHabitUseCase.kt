package com.jolabs.looplog.domain

import com.jolabs.looplog.data.repository.HabitRepository
import com.jolabs.looplog.model.CreateHabit
import javax.inject.Inject

class CreateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke (habit: CreateHabit) : Long {
        return habitRepository.createHabit(habit)
    }
}