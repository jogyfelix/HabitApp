package com.jolabs.looplog.habit.alarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
        private const val WORK_NAME = "reschedule_alarms"
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Received intent: ${intent?.action}")
        
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed, scheduling alarm reschedule work")
            
            context?.let { ctx ->
                try {
                    // Check if WorkManager is available
                    val workManager = WorkManager.getInstance(ctx)
                    Log.d(TAG, "WorkManager instance obtained successfully")
                    
                    // Check if we can create the worker
                    val workRequest = OneTimeWorkRequestBuilder<RescheduleAlarmWorker>().build()
                    Log.d(TAG, "Work request created successfully")
                    
                    workManager.enqueueUniqueWork(
                        WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        workRequest
                    )
                    
                    Log.d(TAG, "Successfully enqueued alarm reschedule work")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to enqueue alarm reschedule work", e)
                    e.printStackTrace()
                }
            } ?: Log.e(TAG, "Context is null, cannot schedule work")
        } else {
            Log.d(TAG, "Not a boot completed intent, ignoring")
        }
    }
}