package com.cases.carefull.features.carefullcommon.components

import android.os.Build
import androidx.annotation.RequiresApi
import com.cases.carefull.domain.model.CalendarViewType
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
data class CalendarState(
	val selectedDate: LocalDate,
	val displayedYearMonth: YearMonth,
	val viewType: CalendarViewType = CalendarViewType.WEEKLY,
	val calendarDates: List<LocalDate>,
	val selectedDateInfo: String,
	val markedDates: Set<LocalDate> = emptySet()
)