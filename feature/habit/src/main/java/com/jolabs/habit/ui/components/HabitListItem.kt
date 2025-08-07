package com.jolabs.habit.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun HabitListItem(
    id: Long,
    name : String,
    description : String,
    longestStreak : String,
    currentStreak : String,
    habitState : ToggleableState,
    shape: Shape,
    onCheckedChange : () -> Unit = {},
    onHabitPress : (habitId : Long) -> Unit = {}
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
            Column(
                modifier = Modifier.clickable{
                    onHabitPress(id)
                }
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "longest streak : $longestStreak times",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(shape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    currentStreak,
                    style = MaterialTheme.typography.headlineLarge,
                    color = textColor
                )
            }

    }
}


@Preview
@Composable
private fun Preview() {
    HabitListItem(
        id= 1,
        name = "Hello",
        description = "Hello how are you",
        longestStreak = "25",
        currentStreak = "20",
        shape = HabitShapes.OctagonShape,
        habitState = ToggleableState.On
    )
}