package com.jolabs.looplog.habit.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jolabs.looplog.habit.alarmManager.HabitAlarmManager
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class RescheduleAlarmWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG = "RescheduleAlarmWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting alarm reschedule work")

        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                WorkerEntryPoint::class.java
            )

            val habitRepository = entryPoint.habitRepository()
            val habitAlarmManager = HabitAlarmManager(applicationContext)

            val habitsResult = habitRepository.getAllHabitsDirect()


            Log.d(TAG, "Found ${habitsResult.size} habits to reschedule")

            habitsResult.forEach { habit ->
                try {
                    // Wait for actual repeat data, not loading state
                    val habitRepeatList = habitRepository.getRepeatDaysFromHabit(habit.id)
                    val firstRepeat = habitRepeatList.first()

                    if (firstRepeat.timeOfDay != null) {
                        Log.d(TAG, "Rescheduling alarm for habit: ${habit.name} at ${firstRepeat.timeOfDay}")

                        habitAlarmManager.scheduleHabitAlarm(
                            habit.id,
                            habit.name,
                            firstRepeat.timeOfDay!!,
                            habitRepeatList.map { it.dayOfWeek }
                        )

                        Log.d(TAG, "Successfully rescheduled alarm for habit: ${habit.name}")
                    } else {
                        Log.d(TAG, "Skipping habit ${habit.name} - no time of day set")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reschedule alarm for habit: ${habit.name}", e)
                }
            }

            Log.d(TAG, "Alarm reschedule work completed successfully")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to reschedule alarms", e)
            Result.failure()
        }
    }
}