package com.jolabs.habit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jolabs.domain.CreateHabitUseCase
import com.jolabs.model.CreateHabit
import com.jolabs.ui.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent : SharedFlow<UIEvent> = _uiEvent


    internal fun onSelectedDayToggle(dayOfWeek: DayOfWeek) {
        _selectedDays.update { current ->
            if(dayOfWeek in current) current - dayOfWeek else current + dayOfWeek
        }
    }

    internal fun onHabitNameChange(name : String) {
        _habitName.value = name
    }

    internal fun onHabitDescriptionChange(description : String) {
        _habitDescription.value = description
    }

    internal fun onTimeOfDayChange(time : Long?) {
        _timeOfDay.value = time
    }

    private fun clearInMemory() {
        _selectedDays.value = emptyList()
        _habitName.value = ""
        _habitDescription.value = ""
        _timeOfDay.value = null
    }

    override fun onCleared() {
        super.onCleared()
        clearInMemory()
    }

    internal fun createHabit() {
        viewModelScope.launch {

            if(_habitName.value.isBlank()){
                _uiEvent.emit(UIEvent.ShowMessage("Habit name cannot be empty"))
                return@launch
            }

            if(_selectedDays.value.isEmpty()){
                _uiEvent.emit(UIEvent.ShowMessage("Please select at least one day of the week"))
                return@launch
            }

            createHabitUseCase(
                habit = CreateHabit(
                    name = _habitName.value,
                    description = _habitDescription.value,
                    daysOfWeek = _selectedDays.value,
                    timeOfDay = _timeOfDay.value,
                    createdAt = System.currentTimeMillis()
                )
            )
            _uiEvent.emit(UIEvent.ShowMessage("Habit created successfully"))
            _uiEvent.emit(UIEvent.NavigateUp)
        }
    }
}