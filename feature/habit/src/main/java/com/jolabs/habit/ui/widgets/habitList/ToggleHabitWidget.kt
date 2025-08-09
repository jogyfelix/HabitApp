package com.jolabs.habit.ui.widgets.habitList

import OpenCreateHabitAction
import android.content.Context
import android.os.Build
import dagger.hilt.android.EntryPointAccessors
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.action.actionParametersOf
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.CheckboxDefaults
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.jolabs.design_system.ui.theme.MyWidgetColorScheme
import com.jolabs.habit.R
import com.jolabs.habit.ui.widgets.WidgetEntryPoint
import com.jolabs.model.HabitBasic
import com.jolabs.model.HabitStatus
import com.jolabs.util.DateUtils.todayEpochDay
import Resource
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.layout.Box
import kotlinx.coroutines.flow.first
import java.time.LocalDate


class ToggleHabitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        // Fetch today's habits once for the current render
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repository = entryPoint.habitRepository()
        val todayEpoch = todayEpochDay()
        val today = LocalDate.now()

        provideContent {

            val resource by repository.getHabitByDate(today.dayOfWeek, todayEpoch)
                .collectAsState(initial = Resource.Loading())
            val habits: List<HabitBasic> = resource.data ?: emptyList()

            GlanceTheme(
                colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    GlanceTheme.colors
                } else {
                    MyWidgetColorScheme.colors
                }
            ) {

                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.background)
                        .padding(8.dp)
                        , verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            "Today", style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = GlanceTheme.colors.onBackground
                            )
                        )

                        Spacer(GlanceModifier.defaultWeight())
                        Image(
                            provider = ImageProvider(R.drawable.add),
                            contentDescription = "Add habit",
                            modifier = GlanceModifier
                                .size(16.dp)
                                .clickable(
                                    actionRunCallback<OpenCreateHabitAction>()
                                )
                        )
                    }

                    Spacer(GlanceModifier.height(8.dp))

                    if (habits.isEmpty()) {
                        Box(
                            modifier = GlanceModifier.fillMaxSize()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No habits for today",
                                style = TextStyle(
                                    color = GlanceTheme.colors.outline
                                )
                            )
                        }
                    } else {
                        LazyColumn {
                            itemsIndexed(habits) { index,item ->
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth()
                                ) {
                                    CheckBox(
                                        checked = item.habitState == HabitStatus.COMPLETED,
                                        onCheckedChange = actionRunCallback<OpenToggleHabitAction>(
                                            actionParametersOf(
                                                HabitIdKey to item.id,
                                                EpochDateKey to todayEpoch,
                                                // For widget, map SKIPPED to NONE so widget stays binary
                                                StatusKey to (if (item.habitState == HabitStatus.COMPLETED) HabitStatus.COMPLETED.name else HabitStatus.NONE.name),
                                            )
                                        ),
                                        text = item.name,
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MyWidgetColorScheme.colors.primary,
                                            uncheckedColor = MyWidgetColorScheme.colors.outline,
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}