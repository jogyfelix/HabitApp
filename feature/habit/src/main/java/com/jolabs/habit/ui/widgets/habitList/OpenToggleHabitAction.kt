package com.jolabs.habit.ui.widgets.habitList

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll

import com.jolabs.habit.ui.widgets.WidgetEntryPoint
import com.jolabs.model.HabitEntryModel
import com.jolabs.model.HabitStatus
import com.jolabs.util.DateUtils
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import Resource
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.delay

class OpenToggleHabitAction: ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val habitId = parameters[HabitIdKey] ?: return
        val date = parameters[EpochDateKey] ?: DateUtils.todayEpochDay()
        val statusName = parameters[StatusKey]

        // Handle potential parsing errors for the status
        val currentStatus = statusName?.let { runCatching { HabitStatus.valueOf(it) }.getOrNull() } ?: HabitStatus.NONE

        // Treat SKIPPED as NONE for widget interactions, and determine the new status concisely
        val newStatus = if (currentStatus == HabitStatus.COMPLETED) HabitStatus.NONE else HabitStatus.COMPLETED

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repo = entryPoint.habitRepository()

        try {
            // Upsert the habit entry with the new status
            repo.upsertHabitEntry(HabitEntryModel(habitId = habitId, date = date, isCompleted = newStatus))
            Log.d("OpenToggleHabitAction", "Successfully updated habitId: $habitId to status: $newStatus")

        } catch (e: Exception) {
            Log.e("OpenToggleHabitAction", "Error updating habitId: $habitId", e)
            // Optionally, handle the error gracefully, e.g., show a toast to the user
        }
    }
}