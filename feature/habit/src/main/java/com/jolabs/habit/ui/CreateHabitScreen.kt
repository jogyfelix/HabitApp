package com.jolabs.habit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

val DAYS_IN_WEEK = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateHabitScreen(
    createHabitViewModel: CreateHabitViewModel = hiltViewModel()
) {

    val color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Add habit") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = "hello",
                    onValueChange = {},
                    label = { Text("Name") }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = "hello",
                    onValueChange = {},
                    label = { Text("Description") }
                )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Repeat On",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.outline)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    DAYS_IN_WEEK.forEach {
                        Box(
                            modifier = Modifier
                                .border(1.dp, color = MaterialTheme.colorScheme.primary,shape = RoundedCornerShape(16.dp))
                                .height(56.dp)
                                .weight(1f)
                                .clip(shape = RoundedCornerShape(16.dp))
                                .background(color)
                                .clickable {  }
                                , contentAlignment = Alignment.Center
                        ) {
                            Text(it,
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }

                }
            }


                Spacer(Modifier.height(10.dp))

                Button(
onClick = {}
                    ,modifier = Modifier
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
        createHabitViewModel = hiltViewModel()
    )
}