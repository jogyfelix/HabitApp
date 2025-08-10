package com.jolabs.looplog.habit.ui.widgets.habitList

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.jolabs.looplog.habit.ui.widgets.WidgetEntryPoint
import com.jolabs.looplog.model.HabitEntryModel
import com.jolabs.looplog.model.HabitStatus
import com.jolabs.looplog.util.DateUtils
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OpenToggleHabitAction: ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val habitId = parameters[HabitIdKey] ?: return
        val date = parameters[EpochDateKey] ?: DateUtils.todayEpochDay()
        val statusName = parameters[StatusKey]

        val currentStatus = statusName?.let { runCatching { HabitStatus.valueOf(it) }.getOrNull() } ?: HabitStatus.NONE

        // Treat SKIPPED as NONE for widget interactions, and determine the new status concisely
        val newStatus = if (currentStatus == HabitStatus.COMPLETED) HabitStatus.NONE else HabitStatus.COMPLETED

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repo = entryPoint.habitRepository()

        try {
            withContext(Dispatchers.IO) {
                repo.upsertHabitEntry(
                    HabitEntryModel(
                        habitId = habitId,
                        date = date,
                        isCompleted = newStatus
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("OpenToggleHabitAction", "Error updating habitId: $habitId", e)
            return
        }

        try {
            withContext(Dispatchers.IO) {
                val day = LocalDate.ofEpochDay(date).dayOfWeek
                repo.getHabitByDate(day, date).first { res ->
                    val list = res.data ?: return@first false
                    list.any { it.id == habitId && it.habitState == newStatus }
                }
            }
        } catch (e: Exception) {
            Log.e("OpenToggleHabitAction", "Error updating habitId: $habitId", e)
            return
        }

        ToggleHabitWidget().update(context, glanceId)
    }
}