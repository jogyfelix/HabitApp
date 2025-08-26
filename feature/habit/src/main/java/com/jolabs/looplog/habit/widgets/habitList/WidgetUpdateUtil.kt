package com.jolabs.looplog.habit.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.jolabs.looplog.habit.widgets.habitList.ToggleHabitWidget
import com.jolabs.looplog.habit.widgets.habitList.encodeHabits
import com.jolabs.looplog.habit.widgets.habitList.toDto
import com.jolabs.looplog.util.DateUtils.todayEpochDay
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetUpdateUtil {
    private const val ACTION_REFRESH_WIDGETS = "com.jolabs.looplog.REFRESH_WIDGETS"

    fun refreshHabitWidgets(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.jolabs.looplog.habit.workers.WorkerEntryPoint::class.java
                )
                val repo = entryPoint.habitRepository()

                val today = LocalDate.now()
                val list = repo.getHabitByDateOnce(today.dayOfWeek, todayEpochDay())
                val json = encodeHabits(list.toDto())

                val manager = GlanceAppWidgetManager(context)
                val ids = manager.getGlanceIds(ToggleHabitWidget::class.java)

                ids.forEach { id ->
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
                        val editable = prefs.toMutablePreferences()
                        editable[stringPreferencesKey("habits_json")] = json
                        editable
                    }
                }


                ToggleHabitWidget().updateAll(context)

            } catch (e: Exception) {
                // Method 2: Broadcast fallback
                val intent = Intent(ACTION_REFRESH_WIDGETS)
                context.sendBroadcast(intent)
            }
        }
    }
}