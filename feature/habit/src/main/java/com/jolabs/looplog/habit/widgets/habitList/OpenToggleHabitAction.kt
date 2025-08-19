package com.jolabs.looplog.habit.widgets.habitList

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.state.updateAppWidgetState
import com.jolabs.looplog.habit.widgets.WidgetEntryPoint
import com.jolabs.looplog.model.HabitEntryModel
import com.jolabs.looplog.model.HabitStatus
import com.jolabs.looplog.util.DateUtils
import dagger.hilt.android.EntryPointAccessors
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

        // Fetch updated list (snapshot), write to this widget state, then refresh
        try {
            val updated = withContext(Dispatchers.IO) {
                val day = LocalDate.ofEpochDay(date).dayOfWeek
                repo.getHabitByDateOnce(day, date)
            }

            val json = encodeHabits(updated.toDto())
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                val editable = prefs.toMutablePreferences()
                editable[stringPreferencesKey("habits_json")] = json
                editable
            }
            ToggleHabitWidget().update(context, glanceId)
        } catch (e: Exception) {
            Log.e("OpenToggleHabitAction", "Error refreshing widget state: $habitId", e)
        }
    }
}