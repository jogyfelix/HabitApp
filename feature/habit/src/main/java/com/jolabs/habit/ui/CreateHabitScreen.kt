package com.jolabs.habit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jolabs.habit.ui.components.WeekSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateHabitScreen(
    createHabitViewModel: CreateHabitViewModel = hiltViewModel()
) {
    val selectedDays by createHabitViewModel.selectedDays.collectAsStateWithLifecycle()
    val habitName by createHabitViewModel.habitName.collectAsStateWithLifecycle()
    val habitDescription by createHabitViewModel.habitDescription.collectAsStateWithLifecycle()
    val timeOfDay by createHabitViewModel.timeOfDay.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Add habit") }) }
    ) { innerPadding ->
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
                onValueChange = {
                    createHabitViewModel.onHabitNameChange(it)
                },
                label = { Text("Name") }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = habitDescription,
                onValueChange = {
                    createHabitViewModel.onHabitDescriptionChange(it)
                },
                label = { Text("Description") }
            )
            WeekSelector(
                selectedDays = selectedDays,
                onDayToggled = createHabitViewModel::onSelectedDayToggle
            )
            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    createHabitViewModel.createHabit()
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)) {
                Text("Save")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
internal fun CreateHabitScreenPreview() {
    CreateHabitScreen(
//        createHabitViewModel = hiltViewModel()
    )
}