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

        if (nextAlarmDateTime.isBefore(now)) {
            nextAlarmDateTime = nextAlarmDateTime.plusDays(1)
        }

        while (!dayOfWeek.contains(nextAlarmDateTime.dayOfWeek)) {
            nextAlarmDateTime = nextAlarmDateTime.plusDays(1)
        }

        return nextAlarmDateTime.toInstant().toEpochMilli()
    }

    fun scheduleHabitAlarm(habitId: Long,habitName:String, timeOfDayMillis: Long, dayOfWeek: List<DayOfWeek>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        dayOfWeek.forEach { day ->
            val nextAlarmTime = getNextAlarmTime(timeOfDayMillis, listOf(day))
            val uniqueRequestId = (habitId.toInt() * 100) + day.ordinal
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueRequestId,
                Intent(context, HabitAlarmReceiver::class.java).apply {
                    putExtra("habitId", habitId)
                    putExtra("habitName", habitName)
                    putExtra("timeOfDay",timeOfDayMillis)
                    putExtra("dayOfWeek",day.toString())
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime,
                pendingIntent
            )
        }
    }

    fun scheduleHabitRepeatAlarm(habitId: Long,habitName:String, timeOfDayMillis: Long, dayOfWeek: DayOfWeek) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val uniqueRequestId = (habitId.toInt() * 100) + dayOfWeek.ordinal
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueRequestId,
                Intent(context, HabitAlarmReceiver::class.java).apply {
                    putExtra("habitId", habitId)
                    putExtra("habitName", habitName)
                    putExtra("timeOfDay",timeOfDayMillis)
                    putExtra("dayOfWeek",dayOfWeek.toString())
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeOfDayMillis,
                pendingIntent
            )

    }


    fun cancelAllHabitAlarms(habitId: Long, dayOfWeek: List<DayOfWeek>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        dayOfWeek.forEach { day ->
            val uniqueRequestId = (habitId.toInt() * 100) + day.ordinal
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueRequestId, // Use the unique ID here
                Intent(context, HabitAlarmReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

}