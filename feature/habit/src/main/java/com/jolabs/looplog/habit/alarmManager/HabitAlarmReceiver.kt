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

const val GROUP_KEY_HABIT_REMINDERS = "com.jolabs.looplog.habit_reminders"
class HabitAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val habitId = intent?.getLongExtra("habitId", 0L)?.toInt() ?: return
        val habitName = intent.getStringExtra("habitName") ?: return


        val fullScreenIntent = Intent(context, AlarmFullScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("habitId", habitId)
            putExtra("habitName", habitName)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, habitId, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(context!!, "looplog_alarm_channel")
            .setSmallIcon(R.drawable.data_empty)
            .setContentTitle("Habit Reminder")
            .setContentText("It's time for $habitName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY_HABIT_REMINDERS)
            .build()

        // Create the summary notification. It's built with the same group key and style.
        val summaryNotification = NotificationCompat.Builder(context, "looplog_alarm_channel")
            .setContentTitle("Multiple Habit Reminders")
            .setContentText("You have new habits to log.")
            .setSmallIcon(R.drawable.data_empty)
            .setGroup(GROUP_KEY_HABIT_REMINDERS)
            .setGroupSummary(true) // Step 3: Mark this as the summary notification
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(habitId, notification)
                notify(0, summaryNotification)
            }
        } else {
            // Permission not granted - maybe log or schedule a retry
        }
    }
}