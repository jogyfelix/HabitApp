package com.jolabs.habit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jolabs.domain.CreateHabitUseCase
import com.jolabs.model.CreateHabit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {
    private val _selectedDays = MutableStateFlow<List<DayOfWeek>>(emptyList())
    val selectedDays : StateFlow<List<DayOfWeek>> = _selectedDays

    private val _habitName = MutableStateFlow("")
    val habitName : StateFlow<String> = _habitName

    private val _habitDescription = MutableStateFlow("")
    val habitDescription : StateFlow<String> = _habitDescription

    private val _timeOfDay : MutableStateFlow<Long?> = MutableStateFlow(null)
    val timeOfDay : StateFlow<Long?> = _timeOfDay


    fun onSelectedDayToggle(dayOfWeek: DayOfWeek) {
        _selectedDays.update { current ->
            if(dayOfWeek in current) current - dayOfWeek else current + dayOfWeek
        }
    }

    fun onHabitNameChange(name : String) {
        _habitName.value = name
    }

    fun onHabitDescriptionChange(description : String) {
        _habitDescription.value = description
    }

    fun onTimeOfDayChange(time : Long) {
        _timeOfDay.value = time
    }

    fun createHabit() {
        viewModelScope.launch {
            createHabitUseCase(
                habit = CreateHabit(
                    name = _habitName.value,
                    description = _habitDescription.value,
                    daysOfWeek = _selectedDays.value,
                    timeOfDay = _timeOfDay.value!!,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}