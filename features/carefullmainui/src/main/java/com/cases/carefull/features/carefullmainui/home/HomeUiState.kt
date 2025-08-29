package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.cases.carefull.domain.model.CalendarViewType
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
data class HomeUiState(
	val selectedDate: LocalDate = LocalDate.now(),
	val displayedYearMonth: YearMonth = YearMonth.now(),
	val viewType: CalendarViewType = CalendarViewType.WEEKLY,
	val calendarDates: List<LocalDate> = emptyList(),
	val isYearMonthPickerVisible: Boolean = false,
	val selectedDateInfo: String = "오늘"
)
