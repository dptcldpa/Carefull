package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.CalendarViewType
import com.cases.carefull.domain.repository.CalendarRepository
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
	private val calendarRepository: CalendarRepository,
	private val exerciseRepository: ExerciseRepository,
	private val dietRepository: DietRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState = _uiState.asStateFlow()
	
	companion object {
		const val START_PAGE = Int.MAX_VALUE / 2
	}
	
	init {
		viewModelScope.launch {
			uiState.map { it.selectedDate to it.viewType }
				.distinctUntilChanged()
				.collect { (date, viewType) ->
					val dates = if (viewType == CalendarViewType.MONTHLY) {
						calendarRepository.getDaysOfMonth(YearMonth.from(date))
					} else {
						calendarRepository.getDaysOfWeek(date)
					}
					_uiState.update {
						it.copy(
							calendarDates = dates,
							displayedYearMonth = YearMonth.from(date)
						)
					}
				}
		}
		calculateAndupdateTargetPage(_uiState.value.selectedDate, _uiState.value.viewType)
		loadInitialData()
		loadBmrAndDietData()
	}
	
	//이거됨
	private fun calculateAndupdateTargetPage(selectedDate: LocalDate, viewType: CalendarViewType) {
		val targetPage = if (viewType == CalendarViewType.MONTHLY) {
			START_PAGE + ChronoUnit.MONTHS.between(
				YearMonth.from(LocalDate.now()),
				YearMonth.from(selectedDate)
			).toInt()
		} else { // WEEKLY
			val startOfReferenceWeek =
				LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
			val startOfSelectedDateWeek =
				selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
			START_PAGE + ChronoUnit.WEEKS.between(
				startOfReferenceWeek,
				startOfSelectedDateWeek
			).toInt()
		}
		_uiState.update { it.copy(pagerTargetPage = targetPage) }
	}
	
	fun onDateSelected(date: LocalDate) {
		_uiState.update {
			it.copy(
				selectedDate = date,
				selectedDateInfo = calculateDaysDifference(date)
			)
		}
		calculateAndupdateTargetPage(date, _uiState.value.viewType)
	}
	
	// 이거됨
	fun onPageScrolled(page: Int) {
		// START_PAGE가 '오늘'이 속한 페이지이므로, page와의 차이가 곧바로 오프셋이 됩니다.
		val pageOffset = (page - START_PAGE).toLong()
		val currentViewType = _uiState.value.viewType
		
		val newDate = if (currentViewType == CalendarViewType.MONTHLY) {
			// --- 월간 뷰 로직 ---
			val originalSelectedDay = _uiState.value.selectedDate.dayOfMonth
			// 1. 목표 '연월'을 '오늘' 기준으로 간단하게 계산합니다.
			val targetYearMonth = YearMonth.from(LocalDate.now()).plusMonths(pageOffset)
			// 2. 해당 월의 마지막 날짜(28, 29, 30, 31 중 하나)를 구합니다.
			val lastDayOfTargetMonth = targetYearMonth.lengthOfMonth()
			// 3. 원래 선택했던 날짜와 마지막 날짜 중 '더 작은 값'을 선택하여 유효한 날짜를 보장합니다.
			// 예: 31일 -> 28일로 자동 변경, 29일 -> 29일로 유지
			val dayToSet = minOf(originalSelectedDay, lastDayOfTargetMonth)
			targetYearMonth.atDay(dayToSet)
			
		} else { // WEEKLY
			LocalDate.now().plusWeeks(pageOffset)
				.with(_uiState.value.selectedDate.dayOfWeek)
		}
		
		_uiState.update {
			it.copy(
				selectedDate = newDate,
				selectedDateInfo = calculateDaysDifference(newDate)
			)
		}
	}
	
	fun onToggleViewType() {
		val newViewType = when (_uiState.value.viewType) {
			CalendarViewType.WEEKLY -> CalendarViewType.MONTHLY
			CalendarViewType.MONTHLY -> CalendarViewType.WEEKLY
		}
		_uiState.update { it.copy(viewType = newViewType) }
		calculateAndupdateTargetPage(_uiState.value.selectedDate, newViewType)
	}
	
	fun onGoToToday() {
		val today = LocalDate.now()
		_uiState.update {
			it.copy(
				selectedDate = today,
				selectedDateInfo = calculateDaysDifference(today)
			)
		}
		calculateAndupdateTargetPage(today, _uiState.value.viewType)
	}
	
	fun showYearMonthPicker() {
		_uiState.update { it.copy(isYearMonthPickerVisible = true) }
	}
	
	fun hideYearMonthPicker() {
		_uiState.update { it.copy(isYearMonthPickerVisible = false) }
	}
	
	fun onYearMonthSelected(yearMonth: YearMonth) {
		val newDate = yearMonth.atDay(1)
		_uiState.update {
			it.copy(
				selectedDate = yearMonth.atDay(1),
				viewType = CalendarViewType.MONTHLY,
				isYearMonthPickerVisible = false,
				selectedDateInfo = calculateDaysDifference(yearMonth.atDay(1))
			)
		}
		calculateAndupdateTargetPage(newDate, CalendarViewType.MONTHLY)
	}
	
	private fun calculateDaysDifference(date: LocalDate): String {
		val daysDifference = date.toEpochDay() - LocalDate.now().toEpochDay()
		return when {
			daysDifference == 0L -> "오늘"
			daysDifference == 1L -> "내일"
			daysDifference == -1L -> "어제"
			daysDifference > 0 -> "${daysDifference}일 후"
			else -> "${-daysDifference}일 전"
		}
	}
	
	fun loadInitialData() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			try {
				
				val dailyExercises = exerciseRepository.getDailyExerciseList()
				_uiState.update {
					it.copy(
						isLoading = false,
						dailyExercise = dailyExercises
					)
				}
			} catch (e: Exception) {
				_uiState.update { it.copy(isLoading = false, isError = true) }
			}
		}
	}
	
	private fun loadBmrAndDietData() {
		viewModelScope.launch {
			dietRepository.getMyBmr("test").collect { bmr ->
				if (bmr != null) {
					_uiState.update {
						it.copy(activityMetabolism = bmr.activityBmr)
					}
				}
			}
		}
		viewModelScope.launch {
			val result = dietRepository.getAllMeal()
			when (result) {
				is DataResourceResult.Success -> {
					val totalCalories = result.data.sumOf { it.kcal }
					_uiState.update {
						it.copy(
							todayTotalCalories = totalCalories,
							hasLoggedMealToday = totalCalories > 0
						)
					}
				}
				
				is DataResourceResult.Error -> {
				}
				
				is DataResourceResult.Loading -> {
				}
			}
		}
	}
}
