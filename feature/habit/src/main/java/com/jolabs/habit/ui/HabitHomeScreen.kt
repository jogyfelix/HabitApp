package com.jolabs.habit.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jolabs.model.HabitBasic
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Locale


@Composable
internal fun HabitHomeRoute(
    habitHomeViewModel: HabitHomeViewModel = hiltViewModel(),
    onCreatePress : () -> Unit
) {
    val habitList = habitHomeViewModel.habitList.collectAsStateWithLifecycle()
    val selectedDate = habitHomeViewModel.selectedDate.collectAsStateWithLifecycle()

    LaunchedEffect(selectedDate.value) {
        habitHomeViewModel.getHabitsForTheDay()
    }

    HabitHomeScreen(
        habitList = habitList.value,
        onCreatePress = onCreatePress,
        onSelectedDateChange = habitHomeViewModel::onSelectedDateChange,
        onSelectedDayChange = habitHomeViewModel::onSelectedDayChange
    )
}


fun calendarToDayOfWeek(calendarDay: Int): DayOfWeek {
    return DayOfWeek.of(if (calendarDay == Calendar.SUNDAY) 7 else calendarDay - 1)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitHomeScreen(
    habitList : List<HabitBasic> = emptyList(),
    onCreatePress : () -> Unit,
    onSelectedDateChange: (Long) -> Unit = {},
    onSelectedDayChange: (DayOfWeek) -> Unit) {

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    var formattedDate by remember {
        mutableStateOf("Today")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
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
                        modifier = Modifier.clickable{
                            showDatePicker = true
                        }, imageVector = Icons.Default.DateRange, contentDescription = "Add")
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreatePress) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }) { innerPadding ->

        if(showDatePicker){
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis

                        if (selectedMillis != null) {
                            val selectedCalendar = Calendar.getInstance().apply {
                                timeInMillis = selectedMillis
                            }

                            val today = Calendar.getInstance()

                            formattedDate = if (
                                selectedCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                selectedCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                            ) {
                                "Today"
                            } else {
                                val formatter = SimpleDateFormat("d MMMM, yyyy", Locale.getDefault())
                                formatter.format(selectedCalendar.time)
                            }

                            val dayOfWeek = calendarToDayOfWeek(selectedCalendar.get(Calendar.DAY_OF_WEEK))
                            onSelectedDayChange(dayOfWeek)
                            onSelectedDateChange(selectedMillis)
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
                    title = { Text("Select Date",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge)},

                )
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            items(habitList) {
                HabitItem(name = it.name, description = it.description, currentStreak = it.currentStreak, longestStreak = it.longestStreak)
                HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
private fun HabitItem(
    name : String,
    description : String,
    longestStreak : String,
    currentStreak : String
) {

    val color = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onPrimary

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Checkbox(checked = false, onCheckedChange = {})
            Column {
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                    , color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "longest streak : $longestStreak days",
                    style = MaterialTheme.typography.bodySmall
                    , color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(0.4f)
                .size(100.dp)
                .padding(start = 8.dp)
                .drawWithCache {
                    val roundedPolygon = RoundedPolygon(
                        numVertices = 6,
                        radius = size.minDimension / 2,
                        centerX = size.width / 2,
                        centerY = size.height / 2
                    )
                    val roundedPolygonPath = roundedPolygon.toPath().asComposePath()
                    onDrawBehind {
                        drawPath(roundedPolygonPath, color = color)
                    }
                }
            ,
            contentAlignment = Alignment.Center

        ) {
            Text(currentStreak,
                style = MaterialTheme.typography.headlineLarge,
                color = textColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun HabitHomeScreenPreview() {
//    HabitHomeScreen({})
}