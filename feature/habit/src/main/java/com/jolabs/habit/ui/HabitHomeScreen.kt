package com.jolabs.habit.ui

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jolabs.habit.ui.components.DatePickerDialog



@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitHomeScreen(
    habitHomeViewModel: HabitHomeViewModel = hiltViewModel()
    ,onCreatePress : () -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val habitList = habitHomeViewModel.habitList.collectAsStateWithLifecycle()

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
                    Text("Today")
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
                onDismiss = { showDatePicker = false },
                onConfirm = {
//                    val pickedTime = Calendar.getInstance().apply {
//                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
//                        set(Calendar.MINUTE, timePickerState.minute)
//                        set(Calendar.SECOND, 0)
//                        set(Calendar.MILLISECOND, 0)
//                    }.timeInMillis
//
//                    onTimeOfDayChange(pickedTime)
                    showDatePicker = false }
            ) {
                DatePicker(
                    state = datePickerState,
                )
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            items(habitList.value) {
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