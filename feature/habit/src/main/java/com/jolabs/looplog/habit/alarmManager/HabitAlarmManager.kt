package com.jolabs.looplog.habit.alarmManager

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime.now
import java.time.LocalDateTime.of
import java.time.ZoneId
import java.time.ZonedDateTime

class HabitAlarmManager(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun checkAlarmPermission() : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }


    fun checkNotificationPermission() : Boolean {
        return  ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getNextAlarmTime(timeOfDayMillis: Long, dayOfWeek: List<DayOfWeek>): Long {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val alarmTime = Instant.ofEpochMilli(timeOfDayMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()

        var nextAlarmDateTime = now.with(alarmTime)

        // Check if the alarm time today has already passed.
        if (nextAlarmDateTime.isBefore(now)) {
            nextAlarmDateTime = nextAlarmDateTime.plusDays(1)
        }

        // Find the next day of the week in the list.
        while (!dayOfWeek.contains(nextAlarmDateTime.dayOfWeek)) {
            nextAlarmDateTime = nextAlarmDateTime.plusDays(1)
        }

        return nextAlarmDateTime.toInstant().toEpochMilli()
    }

    fun scheduleHabitAlarm(habitId: Long,habitName:String, timeOfDayMillis: Long, dayOfWeek: List<DayOfWeek>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Iterate through each day in the list
        dayOfWeek.forEach { day ->
            // Calculate the next alarm time for this specific day
            val nextAlarmTime = getNextAlarmTime(timeOfDayMillis, listOf(day))

            // Create a unique PendingIntent for each alarm
            val uniqueHabitId = habitId.toInt() + day.ordinal // Use a unique ID for each day
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueHabitId,
                Intent(context, HabitAlarmReceiver::class.java).apply {
                    putExtra("habitId", habitId)
                    putExtra("habitName", habitName)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule the alarm
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime,
                pendingIntent
            )
        }
    }


    fun cancelAllHabitAlarms(habitId: Long, dayOfWeek: List<DayOfWeek>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        dayOfWeek.forEach { day ->
            // Create the unique request code for this day
            val uniqueHabitId = habitId.toInt() + day.ordinal

            // Create a PendingIntent that matches the one used for scheduling
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueHabitId, // Use the unique ID here
                Intent(context, HabitAlarmReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            // Cancel the specific alarm for this day
            alarmManager.cancel(pendingIntent)
        }
    }

}