package com.jolabs.habit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jolabs.data.repository.HabitRepository
import com.jolabs.model.HabitBasic
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HabitHomeViewModel @Inject constructor(
    habitRepository: HabitRepository
) : ViewModel() {

    private val _habitList = MutableStateFlow<List<HabitBasic>>(emptyList())
    val habitList : StateFlow<List<HabitBasic>> = _habitList

    init {
        viewModelScope.launch {
             habitRepository.getAllHabits().collect { habits ->
                 _habitList.value = habits
             }

        }
    }
}