package com.cases.carefull.features.carefullmainui.home

import com.cases.carefull.domain.model.CalendarViewType
import com.cases.carefull.domain.model.exercise.ExerciseRecordForDate
import com.cases.carefull.domain.model.exercise.ExerciseType
import java.time.LocalDate
import java.time.YearMonth

data class HomeUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,

    val todayWorkOut: ExerciseType? = null,
    val todayWorkOutCount: Int = 0,
    val todayTotalCalories: Int = 0,
    val targetCalories: Int = 0,

    val selectedDateTotalCalories: Int = 0,
    val selectedDateWorkOutRecords: List<ExerciseRecordForDate> = emptyList(),

    val isYearMonthPickerVisible: Boolean = false,
    val pagerTargetPage: Int = START_PAGE,

    //CalendarState
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val calendarViewType: CalendarViewType = CalendarViewType.MONTHLY,
    val calendarDates: List<LocalDate> = emptyList(),
    val selectedDateInfo: String = "오늘",
    val dietsRecordDates: Set<LocalDate> = emptySet(),
    val dailyExerciseCompletedDates: Set<LocalDate> = emptySet()

) {
    companion object {
        const val START_PAGE = Int.MAX_VALUE / 2
    }
}
