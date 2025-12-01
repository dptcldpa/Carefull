package com.cases.carefull.features.carefullmainui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.CalendarViewType
import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.repository.CalendarRepository
import com.cases.carefull.domain.repository.exercise.WorkOutRecordRepository
import com.cases.carefull.domain.repository.diet.DietRecordRepository
import com.cases.carefull.domain.repository.exercise.TodayWorkOutRepository
import com.cases.carefull.domain.usecase.bmr.GetSavedBmrUseCase
import com.cases.carefull.features.carefullmainui.home.HomeUiState.Companion.START_PAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val workOutRecordRepository: WorkOutRecordRepository,
    private val todayWorkOutRepository: TodayWorkOutRepository,
    private val dietRecordRepository: DietRecordRepository,
    private val getSavedBmrUseCase: GetSavedBmrUseCase,
) : ViewModel() {
	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState = _uiState.asStateFlow()
	
	private val _toastEvent = MutableSharedFlow<String>()
	val toastEvent = _toastEvent.asSharedFlow()
	private val userId = "test"


	companion object {
		const val TODAY_EXERCISE_GOAL = 30
	}
	
	init {
//		observeAllDataFlows()
		observeCalendarChanges()
//		loadOneTimeData()
	}

	@OptIn(ExperimentalCoroutinesApi::class)
//	private fun observeAllDataFlows() {
//		val monthlyDietFlow = _uiState
//			.map { it.displayedYearMonth }
//			.distinctUntilChanged()
//			.flatMapLatest { yearMonth ->
//				dietRecordRepository.getMealsByMonth(yearMonth, userId)
//			}
//		val todayDietFlow = dietRecordRepository.getMealByDate(LocalDate.now(), userId)
//
//		viewModelScope.launch {
//			combine(
//				workOutRepository.getExerciseStatFlow(userId),
//				workOutRepository.getCompletedDailyExerciseDatesFlow(userId),
//				monthlyDietFlow,
//				todayDietFlow,
//				_uiState.map { it.viewType to it.selectedDate }.distinctUntilChanged()
//			) { exerciseStats, completedDates, monthlyDietResult, todayDietResult, (viewType, selectedDate) ->
//
//				val monthlyMealsMap = if (monthlyDietResult is DataResourceResult.Success) {
//					monthlyDietResult.data
//				} else {
//					emptyMap()
//				}
//				val loggedMealDates = monthlyMealsMap.keys
//				val todayMeals = if (todayDietResult is DataResourceResult.Success) {
//					todayDietResult.data
//				} else {
//					emptyList()
//				}
//				val todayCalories = todayMeals.sumOf { it.kcal }
//				val todayExerciseType = _uiState.value.dailyExercise.firstOrNull()
//				val todayCount = calculateTodayExerciseCount(exerciseStats, todayExerciseType)
//				val today = LocalDate.now()
//				if (todayCount >= TODAY_EXERCISE_GOAL && !completedDates.contains(today)) {
//					viewModelScope.launch {
//						workOutRepository.markDailyExerciseAsCompleted(userId, today)
//					}
//				}
//				val exerciseRecordsForDate: List<ExerciseRecordForDate>
//				val totalCaloriesForSelectedDate: Int
//
//				if (viewType == CalendarViewType.MONTHLY) {
//					val dateKey = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
//					exerciseRecordsForDate = exerciseStats.mapNotNull { stat ->
//						stat.dailyCounts[dateKey]?.let { count ->
//							ExerciseRecordForDate(name = stat.exerciseType.type, count = count)
//						}
//					}
//					totalCaloriesForSelectedDate = monthlyMealsMap[selectedDate]?.sumOf { it.kcal } ?: 0
//				} else {
//					exerciseRecordsForDate = emptyList()
//					totalCaloriesForSelectedDate = 0
//				}
//				_uiState.update {
//					it.copy(
//						isLoading = false,
//						todayExerciseCount = todayCount,
//						dailyExerciseCompletedDates = completedDates,
//						selectedDateExerciseRecords = exerciseRecordsForDate,
//						todayTotalCalories = todayCalories,
//						loggedMealDates = loggedMealDates,
//						selectedDateTotalCalories = totalCaloriesForSelectedDate,
//						isError = monthlyDietResult is DataResourceResult.Error || todayDietResult is DataResourceResult.Error
//					)
//				}
//			}.collect()
//		}
//	}

	fun oneMoreTouchExitToast() {
		viewModelScope.launch {
			_toastEvent.emit("한 번 더 누르면 종료됩니다.")
		}
	}
	
//	private fun observeAllDataFlows() {
//		viewModelScope.launch {
//			combine(
//				exerciseRepository.getExerciseStatFlow("test"),
//				exerciseRepository.getCompletedDailyExerciseDatesFlow("test"),
//				dietRecordRepository.getAllMeal(),
//				_uiState.map { it.viewType to it.selectedDate }.distinctUntilChanged()
//			) { exerciseStats, completedDates, dietResult, (viewType, selectedDate) ->
//
//				val mealsByDate =
//					if (dietResult is DataResourceResult.Success) dietResult.data else emptyMap()
//				val todayCalories = (mealsByDate[LocalDate.now()] ?: emptyList()).sumOf { it.kcal }
//				val loggedMealDates = mealsByDate.keys
//
//				val todayExerciseType = _uiState.value.dailyExercise.firstOrNull()
//				val todayCount = calculateTodayExerciseCount(exerciseStats, todayExerciseType)
//
//				val today = LocalDate.now()
//				if (todayCount >= TODAY_EXERCISE_GOAL && !completedDates.contains(today)) {
//					viewModelScope.launch {
//						exerciseRepository.markDailyExerciseAsCompleted("test", today)
//					}
//				}
//				val exerciseRecordsForDate: List<ExerciseRecordForDate>
//				val totalCaloriesForDate: Int
//				if (viewType == CalendarViewType.MONTHLY) {
//					val dateKey = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
//
//					exerciseRecordsForDate = exerciseStats.mapNotNull { stat ->
//						stat.dailyCounts[dateKey]?.let { count ->
//							ExerciseRecordForDate(name = stat.exerciseType.type, count = count)
//						}
//					}
//					totalCaloriesForDate = mealsByDate[selectedDate]?.sumOf { it.kcal } ?: 0
//				} else {
//					exerciseRecordsForDate = emptyList()
//					totalCaloriesForDate = 0
//				}
//				_uiState.update {
//					it.copy(
//						isLoading = false,
//						todayExerciseCount = todayCount,
//						dailyExerciseCompletedDates = completedDates,
//						todayTotalCalories = todayCalories,
//						loggedMealDates = loggedMealDates,
//						isError = dietResult is DataResourceResult.Error,
//						selectedDateExerciseRecords = exerciseRecordsForDate,
//						selectedDateTotalCalories = totalCaloriesForDate
//					)
//				}
//			}.collect()
//		}
//	}
	
//	private fun loadOneTimeData() {
//		viewModelScope.launch {
//			val dailyExercises = todayWorkOutRepository.fetchTodayWorkOut()
//			_uiState.update { it.copy(dailyExercise = dailyExercises) }
//		}
//		viewModelScope.launch {
//			getSavedBmrUseCase("test").collect { bmr ->
//				if (bmr != null) {
//					_uiState.update { it.copy(movementLevelMetabolism = bmr.tdee) }
//				}
//			}
//		}
//		calculateAndUpdateTargetPage(_uiState.value.selectedDate, _uiState.value.viewType)
//	}
	
	private fun observeCalendarChanges() {
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
	}
	
	private fun calculateAndUpdateTargetPage(selectedDate: LocalDate, viewType: CalendarViewType) {
		val targetPage = if (viewType == CalendarViewType.MONTHLY) {
			START_PAGE + ChronoUnit.MONTHS.between(
				YearMonth.from(LocalDate.now()),
				YearMonth.from(selectedDate)
			).toInt()
		} else {
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
		if (date == _uiState.value.selectedDate) return
		_uiState.update {
			it.copy(
				selectedDate = date,
				selectedDateInfo = calculateDaysDifference(date)
			)
		}
		calculateAndUpdateTargetPage(date, _uiState.value.viewType)
	}
	
	fun onPageScrolled(page: Int) {
		val pageOffset = (page - START_PAGE).toLong()
		val currentViewType = _uiState.value.viewType
		val newDate = if (currentViewType == CalendarViewType.MONTHLY) {
			val originalSelectedDay = _uiState.value.selectedDate.dayOfMonth
			val targetYearMonth = YearMonth.from(LocalDate.now()).plusMonths(pageOffset)
			val lastDayOfTargetMonth = targetYearMonth.lengthOfMonth()
			val dayToSet = minOf(originalSelectedDay, lastDayOfTargetMonth)
			targetYearMonth.atDay(dayToSet)
			
		} else {
			LocalDate.now().plusWeeks(pageOffset)
				.with(_uiState.value.selectedDate.dayOfWeek)
		}
		_uiState.update {
			it.copy(
				selectedDate = newDate,
				selectedDateInfo = calculateDaysDifference(newDate),
				pagerTargetPage = page
			)
		}
	}
	
	fun onToggleViewType() {
		val newViewType = when (_uiState.value.viewType) {
			CalendarViewType.WEEKLY -> CalendarViewType.MONTHLY
			CalendarViewType.MONTHLY -> CalendarViewType.WEEKLY
		}
		_uiState.update { it.copy(viewType = newViewType) }
		calculateAndUpdateTargetPage(_uiState.value.selectedDate, newViewType)
	}
	
	fun onGoToToday() {
		val today = LocalDate.now()
		if (today == _uiState.value.selectedDate) return
		_uiState.update {
			it.copy(
				selectedDate = today,
				selectedDateInfo = calculateDaysDifference(today)
			)
		}
		calculateAndUpdateTargetPage(today, _uiState.value.viewType)
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
		calculateAndUpdateTargetPage(newDate, CalendarViewType.MONTHLY)
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
	
	private fun calculateTodayExerciseCount(
		stats: List<ExerciseCollection>,
		todayExercise: ExerciseType?
	): Int {
		if (todayExercise == null) return 0
		val exerciseForToday = stats.find { it.exerciseType == todayExercise }
		val dailyKey = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
		return exerciseForToday?.dailyCounts?.get(dailyKey) ?: 0
	}
}
