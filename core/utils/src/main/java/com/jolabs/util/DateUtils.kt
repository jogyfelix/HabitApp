package com.jolabs.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {
    fun todayEpochDay(): Long = LocalDate.now(ZoneId.systemDefault()).toEpochDay()

    fun formatEpochDay(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        return if (date == LocalDate.now()) "Today"
        else date.format(DateTimeFormatter.ofPattern("d MMMM, yyyy"))
    }

    fun Long.toLocalDateFromEpochDays(): LocalDate =
        LocalDate.ofEpochDay(this)

    fun Long.toLocalDateFromEpochMillis(): LocalDate =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

}