package com.jolabs.looplog.habit.workers

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jolabs.looplog.habit.utils.WidgetUpdateUtil
import com.jolabs.looplog.habit.widgets.habitList.ToggleHabitWidget
import com.jolabs.looplog.habit.widgets.habitList.encodeHabits
import com.jolabs.looplog.habit.widgets.habitList.toDto
import com.jolabs.looplog.util.DateUtils.todayEpochDay
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate

@HiltWorker
class UpdateHabitsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            WidgetUpdateUtil.refreshHabitWidgets(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}