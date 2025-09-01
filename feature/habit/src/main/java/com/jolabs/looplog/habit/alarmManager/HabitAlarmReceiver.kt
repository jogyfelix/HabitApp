package com.jolabs.looplog.habit.alarmManager

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jolabs.looplog.habit.R
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime

const val GROUP_KEY_HABIT_REMINDERS = "com.jolabs.looplog.habit_reminders"
class HabitAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val habitId = intent?.getLongExtra("habitId", 0L) ?: 0L
        val habitName = intent?.getStringExtra("habitName") ?: return
        val timeOfDay = intent.getLongExtra("timeOfDay", 0L)
        val dayOfWeek = intent.getStringExtra("dayOfWeek") ?: return

        val notificationIntent = Intent().apply {
            component = ComponentName("com.jolabs.looplog", "com.jolabs.looplog.MainActivity")
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

            val notification = NotificationCompat.Builder(context!!, "looplog_alarm_channel")
                .setSmallIcon(R.drawable.add_task)
                .setContentTitle("Habit Reminder")
                .setContentText("It's time for $habitName")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY_HABIT_REMINDERS)
                .build()


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                with(NotificationManagerCompat.from(context)) {
                    notify(habitId.toInt(), notification)
                }
            }

        if (timeOfDay > 0 && dayOfWeek.isNotEmpty()) {
            try {
                val currentDateTime = ZonedDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(timeOfDay),
                    ZoneId.systemDefault()
                )
                val sevenDaysFromNow = currentDateTime.plusDays(7).toInstant().toEpochMilli()

                val habitAlarmManager = HabitAlarmManager(context)

                habitAlarmManager.scheduleHabitRepeatAlarm(
                    habitId = habitId,
                    habitName = habitName,
                    timeOfDayMillis = sevenDaysFromNow,
                    dayOfWeek = DayOfWeek.valueOf(dayOfWeek)
                )
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Failed to reschedule alarm", e)
            }
        } else {
            Log.e("AlarmReceiver", "Invalid timeOfDay: $timeOfDay or dayOfWeek: $dayOfWeek")
        }
    }
}