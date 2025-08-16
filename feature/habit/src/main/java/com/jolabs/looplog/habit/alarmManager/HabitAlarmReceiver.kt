package com.jolabs.looplog.habit.alarmManager

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jolabs.looplog.habit.R
import com.jolabs.looplog.habit.ui.alarmUI.AlarmFullScreenActivity

const val notificationId = 1001
class HabitAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val fullScreenIntent = Intent(context, AlarmFullScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("habitId", intent!!.getLongExtra("habitId",0))
            putExtra("habitName", intent.getStringExtra("habitName"))
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context!!, "looplog_alarm_channel")
            .setSmallIcon(R.drawable.data_empty)
            .setContentTitle("Habit Reminder")
            .setContentText("It's time for ${intent!!.getStringExtra("habitName")}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, notification)
            }
        } else {
            // Permission not granted - maybe log or schedule a retry
        }
    }
}