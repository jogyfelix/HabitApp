package com.jolabs.habit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jolabs.data.repository.HabitRepository
import com.jolabs.domain.CreateHabitUseCase
import com.jolabs.model.CreateHabit
import com.jolabs.ui.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val createHabitUseCase: CreateHabitUseCase,
    private val habitRepository: HabitRepository
) : ViewModel() {
    private val _selectedDays = MutableStateFlow<List<DayOfWeek>>(emptyList())
    val selectedDays : StateFlow<List<DayOfWeek>> = _selectedDays

    private val _habitName = MutableStateFlow("")
    val habitName : StateFlow<String> = _habitName

    private val _habitId = MutableStateFlow(0L)
    private val _createdAt = MutableStateFlow(0L)

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

    internal fun getHabit(id : Long) {
        viewModelScope.launch() {
            if(id != 0L)
             {
                _habitId.value = id
                withContext(Dispatchers.IO) {
                    val habit = habitRepository.getHabitById(id)
                    _selectedDays.value = habit?.daysOfWeek ?: emptyList()
                    _habitName.value = habit?.name ?: ""
                    _habitDescription.value = habit?.description ?: ""
                    _timeOfDay.value = habit?.timeOfDay
                    _createdAt.value = habit?.createdAt ?: 0L
                }
            }
        }
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

            withContext(Dispatchers.IO) {
                createHabitUseCase(
                    habit = CreateHabit(
                        id = _habitId.value,
                        name = _habitName.value,
                        description = _habitDescription.value,
                        daysOfWeek = _selectedDays.value,
                        timeOfDay = _timeOfDay.value,
                        createdAt = if(_habitId.value == 0L) System.currentTimeMillis() else _createdAt.value,
                        updatedAt = System.currentTimeMillis(),
                    )
                )
            }
            _uiEvent.emit(UIEvent.ShowMessage("Habit created successfully"))
            _uiEvent.emit(UIEvent.NavigateUp)
        }
    }
}