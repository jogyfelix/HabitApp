package com.jolabs.looplog.habit.alarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jolabs.looplog.habit.workers.RescheduleAlarmWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
        private const val WORK_NAME = "reschedule_alarms"
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            context?.let { ctx ->
                try {
                    val workManager = WorkManager.getInstance(ctx)
                    val workRequest = OneTimeWorkRequestBuilder<RescheduleAlarmWorker>().build()
                    workManager.enqueueUniqueWork(
                        WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        workRequest
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to enqueue alarm reschedule work", e)
                    e.printStackTrace()
                }
            }
        }
    }
}