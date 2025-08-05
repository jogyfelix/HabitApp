package com.jolabs.habit.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jolabs.habit.ui.components.HabitListItem
import com.jolabs.habit.ui.components.HabitShapes
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitStatus
import com.jolabs.util.DateUtils.formatEpochDay
import com.jolabs.util.DateUtils.todayEpochDay
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
internal fun HabitHomeRoute(
    habitHomeViewModel: HabitHomeViewModel = hiltViewModel(),
    onCreatePress: () -> Unit
) {
    val habitList = habitHomeViewModel.habitList.collectAsStateWithLifecycle()
    val selectedDate = habitHomeViewModel.selectedDate.collectAsStateWithLifecycle()

    LaunchedEffect(selectedDate.value) {
        habitHomeViewModel.getHabitsForTheDay()
    }

    HabitHomeScreen(
        habitList = habitList.value,
        onCreatePress = onCreatePress,
        selectedDate = selectedDate.value,
        onSelectedDateChange = habitHomeViewModel::onSelectedDateChange,
        onSelectedDayChange = habitHomeViewModel::onSelectedDayChange,
        updateHabit = habitHomeViewModel::updateHabitEntry
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitHomeScreen(
    habitList: List<HabitBasic> = emptyList(),
    onCreatePress: () -> Unit,
    selectedDate: Long = todayEpochDay(),
    onSelectedDateChange: (Long) -> Unit = {},
    onSelectedDayChange: (DayOfWeek) -> Unit,
    updateHabit : (Long, Long, HabitStatus) -> Unit = { _, _, _ ->}
) {

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = remember(selectedDate) {
            LocalDate.ofEpochDay(selectedDate).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
    )

    val formattedDate = remember(selectedDate) {
        formatEpochDay(selectedDate)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(title = {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(formattedDate)
                    Icon(
                        modifier = Modifier.clickable {
                            showDatePicker = true
                        }, imageVector = Icons.Default.DateRange, contentDescription = "Add"
                    )
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreatePress) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }) { innerPadding ->

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onSelectedDayChange(selectedDate.dayOfWeek)
                            onSelectedDateChange(selectedDate.toEpochDay())
                        }
                        showDatePicker = false
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) {
                        Text("Cancel")
                    }
                },
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    title = {
                        Text(
                            "Select Date",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },

                    )
            }
        }

        val shapeCache = remember { mutableMapOf<String, Shape>() }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            items(habitList) {
                val shape = shapeCache.getOrPut(it.id.toString()) {
                    HabitShapes.funShapes.random()
                }

                HabitListItem(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    currentStreak = it.currentStreak,
                    longestStreak = it.longestStreak,
                    habitState =  when(it.habitState) {
                        HabitStatus.NONE -> ToggleableState.Off
                        HabitStatus.COMPLETED -> ToggleableState.On
                        HabitStatus.SKIPPED -> ToggleableState.Indeterminate
                    },
                    shape = shape,
                    onCheckedChange = {
                        val newStatus = when (it.habitState) {
                            HabitStatus.NONE -> HabitStatus.COMPLETED
                            HabitStatus.COMPLETED -> HabitStatus.SKIPPED
                            HabitStatus.SKIPPED -> HabitStatus.NONE
                        }
                        println(newStatus)
                            updateHabit(
                                it.id,
                                selectedDate,
                                newStatus
                            )
                    })
                HorizontalDivider(
                    thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun HabitHomeScreenPreview() {
    HabitHomeScreen(
        habitList = emptyList(),
        onCreatePress = { },
        onSelectedDateChange = {},
        onSelectedDayChange = {}
    )
}