package com.jolabs.habit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitHomeScreen(onCreatePress : () -> Unit) {
    val color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                    Icon(Icons.Default.DateRange, contentDescription = "Add")
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreatePress) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
//            items(20) {
//                HabitItem(color)
//                HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp),
//                    color = MaterialTheme.colorScheme.outlineVariant)
//            }
        }
    }
}

@Composable
private fun HabitItem(color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = false, onCheckedChange = {})
            Column {
                Text(
                    "Drink water",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Drink water to keep hydrated",
                    style = MaterialTheme.typography.bodyMedium
                    , color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "longest streak : 10 days",
                    style = MaterialTheme.typography.bodySmall
                    , color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Box(
            modifier = Modifier
                .size(100.dp)
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
                },
            contentAlignment = Alignment.Center

        ) {
            Text("100",
                style = MaterialTheme.typography.headlineLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun HabitHomeScreenPreview() {
    HabitHomeScreen({})
}