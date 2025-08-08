package com.jolabs.habit.ui

import Resource
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jolabs.habit.ui.components.HabitListItem
import com.jolabs.design_system.ui.theme.HabitShapes
import com.jolabs.habit.R
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitStatus
import com.jolabs.util.DateUtils.formatEpochDay
import com.jolabs.util.DateUtils.todayEpochDay
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset


@Composable
internal fun HabitHomeRoute(
    habitHomeViewModel: HabitHomeViewModel = hiltViewModel(),
    onCreatePress: (habitId: Long) -> Unit
) {
    val habitList = habitHomeViewModel.habitList.collectAsStateWithLifecycle()
    val selectedDate = habitHomeViewModel.selectedDate.collectAsStateWithLifecycle()



    HabitHomeScreen(
        habitList = habitList.value,
        onCreatePress = onCreatePress,
        selectedDate = selectedDate.value,
        onSelectedDateChange = habitHomeViewModel::onSelectedDateChange,
        onSelectedDayChange = habitHomeViewModel::onSelectedDayChange,
        updateHabit = habitHomeViewModel::updateHabitEntry,
        deleteHabitPress = habitHomeViewModel::deleteHabit
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitHomeScreen(
    habitList: Resource<List<HabitBasic>> = Resource.Loading(),
    onCreatePress: (habitId: Long) -> Unit,
    deleteHabitPress: (habitId: Long) -> Unit,
    selectedDate: Long = todayEpochDay(),
    onSelectedDateChange: (Long) -> Unit = {},
    onSelectedDayChange: (DayOfWeek) -> Unit,
    updateHabit: (Long, Long, HabitStatus) -> Unit = { _, _, _ -> }
) {

    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteHabitId by remember { mutableLongStateOf(0L) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = remember(selectedDate) {
            LocalDate.ofEpochDay(selectedDate).atStartOfDay(ZoneOffset.UTC).toInstant()
                .toEpochMilli()
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
            FloatingActionButton(onClick = { onCreatePress(0) }) {
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

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                },
                title = {
                    Text(text = "Delete habit?")
                },
                text = {
                    Text(text = "This will permanently delete the habit and all of its progress. This action cannot be undone.")
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            deleteHabitPress(deleteHabitId)
                            showDeleteDialog = false
                            deleteHabitId = 0L
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Dismiss")
                    }
                }
            )
        }

        val shapeCache = remember { mutableMapOf<String, Shape>() }
        when (val habitListState = habitList) {
            is Resource.Error<*> -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier.padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.data_empty),
                            contentDescription = "My custom icon",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(88.dp)
                        )
                        Text(
                            text = "Oops! Something went wrong while loading your habits. Don't worry, we're on it. Please try again in a bit!",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is Resource.Loading<*> -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Success<*> -> {
                if (!habitListState.data.isNullOrEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                    ) {
                        itemsIndexed(habitListState.data!!) { index, item ->
                            val shape = shapeCache.getOrPut(item.id.toString()) {
                                HabitShapes.funShapes.random()
                            }

                            HabitListItem(
                                id = item.id,
                                name = item.name,
                                description = item.description,
                                currentStreak = item.currentStreak,
                                longestStreak = item.longestStreak,
                                habitState = when (item.habitState) {
                                    HabitStatus.NONE -> ToggleableState.Off
                                    HabitStatus.COMPLETED -> ToggleableState.On
                                    HabitStatus.SKIPPED -> ToggleableState.Indeterminate
                                },
                                shape = shape,
                                modifier = Modifier.padding(bottom = if (index == habitListState.data!!.lastIndex) 100.dp else 0.dp),
                                onCheckedChange = {
                                    val newStatus = when (item.habitState) {
                                        HabitStatus.NONE -> HabitStatus.COMPLETED
                                        HabitStatus.COMPLETED -> HabitStatus.SKIPPED
                                        HabitStatus.SKIPPED -> HabitStatus.NONE
                                    }
                                    updateHabit(
                                        item.id,
                                        selectedDate,
                                        newStatus
                                    )
                                },
                                onHabitPress = onCreatePress,
                                deleteHabitPress = { id ->
                                    showDeleteDialog = true
                                    deleteHabitId = id
                                }
                            )

                            if (index < habitListState.data!!.lastIndex) {
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier.padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.event_list),
                                contentDescription = "My custom icon",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(88.dp)
                            )
                            Text(
                                text = "No habits scheduled for today. Ready to start a new one?",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )

                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun HabitHomeScreenPreview() {
    HabitHomeScreen(
        habitList = Resource.Loading(),
        onCreatePress = { },
        onSelectedDateChange = {},
        onSelectedDayChange = {},
        deleteHabitPress = {}
    )
}