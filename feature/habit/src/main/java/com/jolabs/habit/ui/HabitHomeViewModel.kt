package com.jolabs.habit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jolabs.data.repository.HabitRepository
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitEntryModel
import com.jolabs.model.HabitStatus
import com.jolabs.util.DateUtils.todayEpochDay
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

@HiltViewModel
class HabitHomeViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _habitList = MutableStateFlow<List<HabitBasic>>(emptyList())
    val habitList : StateFlow<List<HabitBasic>> = _habitList

    private val _selectedDate : MutableStateFlow<Long> = MutableStateFlow(todayEpochDay())
    val selectedDate : StateFlow<Long> = _selectedDate

    private val _selectedDay : MutableStateFlow<DayOfWeek> = MutableStateFlow(LocalDate.now().dayOfWeek)
//    val selectedDay : StateFlow<DayOfWeek> = _selectedDay

    internal fun onSelectedDayChange(day : DayOfWeek) {
        _selectedDay.value = day
    }

    internal fun onSelectedDateChange(date : Long) {
        _selectedDate.value = date
    }

    internal fun getHabitsForTheDay() {
        viewModelScope.launch {
            habitRepository.getHabitByDate(dayOfWeek = _selectedDay.value,   _selectedDate.value).collect { habits ->
                _habitList.value = habits
                println(habits)
            }
        }
    }

    internal fun updateHabitEntry(habitId : Long, date : Long, status : HabitStatus) {
        viewModelScope.launch {
            habitRepository.upsertHabitEntry(
                HabitEntryModel(
                    habitId = habitId,
                    date = date,
                    isCompleted = status
                )
            )
        }
    }
}