package com.jolabs.habit.ui

import Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jolabs.data.repository.HabitRepository
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitEntryModel
import com.jolabs.model.HabitStatus
import com.jolabs.ui.UIEvent
import com.jolabs.util.DateUtils.todayEpochDay
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

@HiltViewModel
class HabitHomeViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {


    private val _selectedDate : MutableStateFlow<Long> = MutableStateFlow(todayEpochDay())
    val selectedDate : StateFlow<Long> = _selectedDate

    private val _selectedDay : MutableStateFlow<DayOfWeek> = MutableStateFlow(LocalDate.now().dayOfWeek)
    val selectedDay : StateFlow<DayOfWeek> = _selectedDay

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent : SharedFlow<UIEvent> = _uiEvent

    @OptIn(ExperimentalCoroutinesApi::class)
    val habitList: StateFlow<Resource<List<HabitBasic>>> = combine(
    selectedDate,
    selectedDay
    ) { date, day ->
        Pair(date, day)
    }
    .flatMapLatest { (date, day) ->
        habitRepository.getHabitByDate(day, date)
    }
    .stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = Resource.Loading()
    )

    internal fun onSelectedDayChange(day : DayOfWeek) {
        _selectedDay.value = day
    }

    internal fun onSelectedDateChange(date : Long) {
        _selectedDate.value = date
    }


    internal fun updateHabitEntry(habitId : Long, date : Long, status : HabitStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            habitRepository.upsertHabitEntry(
                HabitEntryModel(
                    habitId = habitId,
                    date = date,
                    isCompleted = status
                )
            )
        }
    }

    internal fun deleteHabit(habitId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val habit = habitRepository.deleteHabit(habitId)
            if(habit == 1) {
                _uiEvent.emit(UIEvent.ShowMessage("Habit deleted successfully"))
            }

        }

    }
}