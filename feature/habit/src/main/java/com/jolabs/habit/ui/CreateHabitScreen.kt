package com.jolabs.habit.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jolabs.habit.ui.components.PickerDialog
import com.jolabs.habit.ui.components.WeekSelector
import com.jolabs.ui.UIEvent
import java.time.DayOfWeek
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateHabitRoute(
    createHabitViewModel: CreateHabitViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit = {}
) {
    val context = LocalContext.current
    val selectedDays by createHabitViewModel.selectedDays.collectAsStateWithLifecycle()
    val habitName by createHabitViewModel.habitName.collectAsStateWithLifecycle()
    val habitDescription by createHabitViewModel.habitDescription.collectAsStateWithLifecycle()
    val timeOfDay by createHabitViewModel.timeOfDay.collectAsStateWithLifecycle()

    val calendar = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = false,
    )

    LaunchedEffect(Unit) {
        createHabitViewModel.uiEvent.collect { event ->
            when(event){
                UIEvent.NavigateUp -> {
                    onNavigateUp()
                }
                is UIEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CreateHabitScreen(
        selectedDays = selectedDays,
        habitName = habitName,
        habitDescription = habitDescription,
        timeOfDay = timeOfDay,
        timePickerState = timePickerState,
        onNavigateUp = onNavigateUp,
        onSelectedDayToggle = createHabitViewModel::onSelectedDayToggle,
        onHabitNameChange = createHabitViewModel::onHabitNameChange,
        onHabitDescriptionChange = createHabitViewModel::onHabitDescriptionChange,
        onTimeOfDayChange = createHabitViewModel::onTimeOfDayChange,
        createHabitPress = createHabitViewModel::createHabit
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateHabitScreen(
    selectedDays : List<DayOfWeek> = emptyList(),
    habitName : String = "",
    habitDescription : String = "",
    timeOfDay : Long? = null,
    timePickerState : TimePickerState = rememberTimePickerState(),
    onNavigateUp : () -> Unit = {},
    onSelectedDayToggle : (DayOfWeek) -> Unit = {},
    onHabitNameChange : (String) -> Unit = {},
    onHabitDescriptionChange : (String) -> Unit = {},
    onTimeOfDayChange : (Long) -> Unit = {},
    createHabitPress : () -> Unit = {},
) {

    var showTimePicker by remember { mutableStateOf(false) }
    val timeText = remember(timeOfDay) {
        timeOfDay?.let {
            val cal = Calendar.getInstance().apply { timeInMillis = it }
            val hour = cal.get(Calendar.HOUR)
            val minute = cal.get(Calendar.MINUTE)
            val amPm = if (cal.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
            val displayHour = if (hour == 0) 12 else hour
            "%02d:%02d %s".format(displayHour, minute, amPm)
        } ?: ""
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Add habit") },
            navigationIcon = {
                IconButton(onClick = { onNavigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                }
            }
            ) }
    ) { innerPadding ->

       if(showTimePicker){
           PickerDialog(
               onDismiss = { showTimePicker = false },
               onConfirm = {
                   val pickedTime = Calendar.getInstance().apply {
                       set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                       set(Calendar.MINUTE, timePickerState.minute)
                       set(Calendar.SECOND, 0)
                       set(Calendar.MILLISECOND, 0)
                   }.timeInMillis

                   onTimeOfDayChange(pickedTime)
                   showTimePicker = false }
           ) {
               TimePicker(
                   state = timePickerState,
               )
           }
       }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = habitName,
                singleLine = true,
                onValueChange = {
                    onHabitNameChange(it)
                },
                label = { Text("Name") }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = habitDescription,
                onValueChange = {
                   onHabitDescriptionChange(it)
                },
                label = { Text("Description") }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showTimePicker = true
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = OutlinedTextFieldDefaults.colors().focusedContainerColor,
                    disabledTextColor = OutlinedTextFieldDefaults.colors().focusedTextColor,
                    disabledLabelColor = OutlinedTextFieldDefaults.colors().unfocusedLabelColor,
                    disabledLeadingIconColor = OutlinedTextFieldDefaults.colors().focusedLeadingIconColor,
                    disabledTrailingIconColor = OutlinedTextFieldDefaults.colors().focusedTrailingIconColor,
                    disabledPlaceholderColor = OutlinedTextFieldDefaults.colors().focusedPlaceholderColor,
                    disabledBorderColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor,
                ),
                value = timeText,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Set a Time (Optional)") }
            )
            WeekSelector(
                selectedDays = selectedDays,
                onDayToggled = onSelectedDayToggle
            )
            Spacer(Modifier.height(10.dp))

            Button(
                onClick = createHabitPress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)) {
                Text("Save")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
internal fun CreateHabitScreenPreview() {
    CreateHabitScreen()
}