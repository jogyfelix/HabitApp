package com.jolabs.domain

import com.jolabs.data.repository.HabitRepository

class CreateHabitUseCase constructor(
    private val habitRepository: HabitRepository
) {

}