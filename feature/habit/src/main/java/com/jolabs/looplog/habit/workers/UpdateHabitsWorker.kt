package com.jolabs.looplog.habit.workers

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jolabs.looplog.habit.widgets.habitList.ToggleHabitWidget
import com.jolabs.looplog.habit.widgets.habitList.encodeHabits
import com.jolabs.looplog.habit.widgets.habitList.toDto
import com.jolabs.looplog.util.DateUtils.todayEpochDay
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate

@HiltWorker
class UpdateHabitsWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                WorkerEntryPoint::class.java
            )
            val repo = entryPoint.habitRepository()

            val today = LocalDate.now()
            val list = repo.getHabitByDateOnce(today.dayOfWeek, todayEpochDay())
            val json = encodeHabits(list.toDto())

            val manager = GlanceAppWidgetManager(applicationContext)
            val ids = manager.getGlanceIds(ToggleHabitWidget::class.java)
            ids.forEach { id ->
                updateAppWidgetState(applicationContext, PreferencesGlanceStateDefinition, id) { prefs ->
                    val editable = prefs.toMutablePreferences()
                    editable[stringPreferencesKey("habits_json")] = json
                    editable
                }
            }
            ToggleHabitWidget().updateAll(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}