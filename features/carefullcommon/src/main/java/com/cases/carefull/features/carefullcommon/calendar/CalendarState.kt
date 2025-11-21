package com.cases.carefull.features.carefullcommon.calendar

import com.cases.carefull.domain.model.CalendarViewType
import java.time.LocalDate
import java.time.YearMonth

data class CalendarState(
    val selectedDate: LocalDate,
    val displayedYearMonth: YearMonth,
    val viewType: CalendarViewType = CalendarViewType.MONTHLY,
    val calendarDates: List<LocalDate>,
    val selectedDateInfo: String,
    val markedDates: Set<LocalDate> = emptySet(),
    val dailyExerciseCompletedDates: Set<LocalDate> = emptySet()
)