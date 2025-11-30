package com.cases.carefull.domain.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

object WorkoutDateUtils {

    fun getDailyKey(date: LocalDate = LocalDate.now()): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @Suppress("DefaultLocale")
    fun getWeeklyKey(date: LocalDate = LocalDate.now()): String {
        val weekFields = WeekFields.ISO
        val weekBasedYear = date.get(weekFields.weekBasedYear())
        val weekOfYear = date.get(weekFields.weekOfWeekBasedYear())
        return String.format("%04d-W%02d", weekBasedYear, weekOfYear)
    }
}
