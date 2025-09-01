package com.jolabs.looplog.habit.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jolabs.looplog.habit.alarmManager.HabitAlarmManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class RescheduleAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG = "RescheduleAlarmWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                WorkerEntryPoint::class.java
            )

            val habitRepository = entryPoint.habitRepository()
            val habitAlarmManager = HabitAlarmManager(applicationContext)

            val habitsResult = habitRepository.getAllHabitsDirect()

            habitsResult.forEach { habit ->
                try {
                    val habitRepeatList = habitRepository.getRepeatDaysFromHabit(habit.id)
                    val firstRepeat = habitRepeatList.first()

                    if (firstRepeat.timeOfDay != null) {
                        habitAlarmManager.scheduleHabitAlarm(
                            habit.id,
                            habit.name,
                            firstRepeat.timeOfDay!!,
                            habitRepeatList.map { it.dayOfWeek }
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reschedule alarm for habit: ${habit.name}", e)
                }
            }
        Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to reschedule alarms", e)
            Result.failure()
        }
    }
}