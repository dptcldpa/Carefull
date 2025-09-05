package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.cases.carefull.domain.model.CalendarViewType
import com.cases.carefull.domain.model.exercise.ExerciseRecordForDate
import com.cases.carefull.domain.model.exercise.ExerciseType
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
data class HomeUiState(
	val selectedDate: LocalDate = LocalDate.now(),
	val displayedYearMonth: YearMonth = YearMonth.now(),
	val viewType: CalendarViewType = CalendarViewType.WEEKLY,
	val calendarDates: List<LocalDate> = emptyList(),
	val isYearMonthPickerVisible: Boolean = false,
	val selectedDateInfo: String = "오늘",
	
	val isLoading: Boolean = false,
	val isError: Boolean = false,
	val dailyExercise: List<ExerciseType> = emptyList(),
	val selectedExercise: ExerciseType? = null,
	val showDialog: Boolean = false,
	
	val todayTotalCalories: Int = 0,
	val activityMetabolism: Int = 0,
	
	val loggedMealDates: Set<LocalDate> = emptySet(),
	val dailyExerciseCompletedDates: Set<LocalDate> = emptySet(),
	val todayExerciseCount: Int = 0,
	val hasLoggedMealToday: Boolean = false,
	
	val selectedDateExerciseRecords: List<ExerciseRecordForDate> = emptyList(),
	val selectedDateTotalCalories: Int = 0,
	
	val pagerTargetPage: Int = START_PAGE
) {
	companion object {
		const val START_PAGE = Int.MAX_VALUE / 2
	}
}
