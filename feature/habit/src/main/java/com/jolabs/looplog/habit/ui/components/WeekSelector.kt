package com.jolabs.looplog.habit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek

@Composable
fun WeekSelector(
    selectedDays: List<DayOfWeek>,
    onDayToggled: (DayOfWeek) -> Unit = {},
    toggleAllDays: (Boolean) -> Unit = {}
) {


    val (checkedState, onStateChange) = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "Repeat On",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DayOfWeek.entries.forEach { day ->
                val isSelected = day in selectedDays
                DayItem(
                    modifier = Modifier.weight(1f), day, isSelected,
                    { onDayToggled(day) }
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .toggleable(
                    value = checkedState,
                    onValueChange = {
                        onStateChange(it)
                        toggleAllDays(it)
                    },
                    role = Role.Checkbox
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkedState,
                onCheckedChange = null
            )
            Text(
                text = "Everyday",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun DayItem(
    modifier: Modifier = Modifier,
    day: DayOfWeek,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected)
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
    else
        MaterialTheme.colorScheme.background

    val textColor = if (isSelected)
        MaterialTheme.colorScheme.tertiary
    else
        MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            day.name.take(3),
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekSelectorPreview() {
    WeekSelector(
        selectedDays = emptyList(),
        onDayToggled = {}
    )
}