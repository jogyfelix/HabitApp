package com.jolabs.habit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import com.jolabs.model.HabitStatus

@Composable
internal fun HabitListItem(
    name : String,
    description : String,
    longestStreak : String,
    currentStreak : String,
    habitState : ToggleableState,
    onCheckedChange : () -> Unit = {}
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
            TriStateCheckbox(state = habitState, onClick = onCheckedChange)
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
                    "longest streak : $longestStreak times",
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


@Preview
@Composable
private fun Preview() {
    HabitListItem(
        name = "Hello",
        description = "Hello how are you",
        longestStreak = "25",
        currentStreak = "20",
        habitState = ToggleableState.On
    )
}