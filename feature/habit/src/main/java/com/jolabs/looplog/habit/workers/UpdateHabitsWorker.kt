package com.jolabs.looplog.habit.workers

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jolabs.looplog.habit.widgets.habitList.ToggleHabitWidget

@HiltWorker
class UpdateHabitsWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
       return try {
           ToggleHabitWidget().updateAll(applicationContext)
           Result.success()
       } catch (e: Exception){
           Result.failure()
       }
    }
}