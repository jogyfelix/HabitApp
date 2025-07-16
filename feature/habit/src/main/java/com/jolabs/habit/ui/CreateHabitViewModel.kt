package com.jolabs.habit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jolabs.domain.CreateHabitUseCase
import com.jolabs.model.CreateHabit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {

    fun createHabit() {
        viewModelScope.launch {
            createHabitUseCase(
                habit = CreateHabit(
                    name = "Praise God",
                    description = "Always and everyday",
                    daysOfWeek = emptyList(),
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}