package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.CalendarViewType
import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.repository.CalendarRepository
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.features.carefullmainui.home.HomeUiState.Companion.START_PAGE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
	private val calendarRepository: CalendarRepository,
	private val exerciseRepository: ExerciseRepository,
	private val dietRepository: DietRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState = _uiState.asStateFlow()
	
	companion object {
		const val TODAY_EXERCISE_GOAL = 30
	}
	
	init {
		observeAllDataFlows()     // 모든 실시간 데이터 스트림 구독
		observeCalendarChanges()  // UI 이벤트 관련 데이터 스트림 구독
		loadOneTimeData()
		
//		loadStaticData()
//		observeRealtimeData()
//		observeAllMeals()
//		observeCalendarChanges()
//		refreshHomeData()
//		loadBmrData()
//		loadDietData()
//		loadInitialData()
	}
	
	private fun observeAllDataFlows() {
		// 1. 운동 기록, 완료 날짜, 식단 기록 Flow를 하나로 합칩니다.
		viewModelScope.launch {
			combine(
				exerciseRepository.getExerciseStatFlow("test"),
				exerciseRepository.getCompletedDailyExerciseDatesFlow("test"),
				dietRepository.getAllMeal() // 이 함수가 Flow<DataResourceResult<...>>를 반환한다고 가정
			) { exerciseStats, completedDates, dietResult ->
				// 3개의 Flow 중 하나라도 새로운 값을 방출하면 이 블록이 실행됩니다.
				
				// 식단 데이터 처리
				val todayCalories: Int
				val loggedMealDates: Set<LocalDate>
				if (dietResult is DataResourceResult.Success) {
					val mealsByDate = dietResult.data
					todayCalories = (mealsByDate[LocalDate.now()] ?: emptyList()).sumOf { it.kcal }
					loggedMealDates = mealsByDate.keys
				} else {
					todayCalories = _uiState.value.todayTotalCalories // 이전 값 유지 또는 0
					loggedMealDates = _uiState.value.loggedMealDates // 이전 값 유지 또는 emptySet()
				}
				
				// 운동 횟수 계산
				val todayExerciseType = _uiState.value.dailyExercise.firstOrNull()
				val todayCount = calculateTodayExerciseCount(exerciseStats, todayExerciseType)
				
				// 최종적으로 모든 데이터를 한 번에 업데이트
				_uiState.update {
					it.copy(
						isLoading = false,
						todayExerciseCount = todayCount,
						dailyExerciseCompletedDates = completedDates,
						todayTotalCalories = todayCalories,
						loggedMealDates = loggedMealDates,
						isError = dietResult is DataResourceResult.Error // 에러 상태 업데이트
					)
				}
				
			}.collect() // combine Flow를 수집 시작
		}
	}
	
	private fun loadOneTimeData() {
		viewModelScope.launch {
			// 오늘의 운동 종류 (앱 실행 시 한 번만 결정됨)
			val dailyExercises = exerciseRepository.getDailyExerciseList()
			_uiState.update { it.copy(dailyExercise = dailyExercises) }
		}
		
		// BMR (자주 바뀌지 않음)
		viewModelScope.launch {
			dietRepository.getMyBmr("test").collect { bmr ->
				if (bmr != null) {
					_uiState.update { it.copy(activityMetabolism = bmr.activityBmr) }
				}
			}
		}
		
		// 초기 페이지 계산 (UI와 관련 있으므로 여기에 두거나, observeCalendarChanges와 합쳐도 됨)
		calculateAndupdateTargetPage(_uiState.value.selectedDate, _uiState.value.viewType)
	}
	
	// [추가] 실시간 데이터 스트림을 구독하는 함수
	private fun observeRealtimeData() {
		viewModelScope.launch {
			// 운동 기록 Flow 구독
			exerciseRepository.getExerciseStatFlow("test").collect { allExerciseStats ->
				val todayExerciseType = _uiState.value.dailyExercise.firstOrNull()
				val todayCount = calculateTodayExerciseCount(allExerciseStats, todayExerciseType)
				
				_uiState.update {
					it.copy(
						isLoading = false,
						todayExerciseCount = todayCount
					)
				}
			}
		}
		viewModelScope.launch {
			exerciseRepository.getCompletedDailyExerciseDatesFlow("test").collect { completedDates ->
				_uiState.update { it.copy(dailyExerciseCompletedDates = completedDates) }
			}
		}
	}
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
	
	// [추가] 한 번만 로드하면 되는 데이터를 가져오는 함수
	private fun loadStaticData() {
		viewModelScope.launch {
			val dailyExercises = exerciseRepository.getDailyExerciseList()
			val completedDates = exerciseRepository.getCompletedDailyExerciseDates("test")
			_uiState.update {
				it.copy(
					dailyExercise = dailyExercises,
					dailyExerciseCompletedDates = completedDates
				)
			}
		}
	}
	
	private fun observeAllMeals() {
		viewModelScope.launch {
			dietRepository.getAllMeal().collect { result ->
				_uiState.update { it.copy(isLoading = true) }
				when (result) {
					is DataResourceResult.Success -> {
						val mealsByDate = result.data
						val today = LocalDate.now()
						val todayMeals = mealsByDate[today] ?: emptyList()
						val todayCalories = todayMeals.sumOf { it.kcal }
						val datesWithLoggedMeals = mealsByDate.keys
						
						_uiState.update {
							it.copy(
								isLoading = false,
								isError = false,
								todayTotalCalories = todayCalories,
								loggedMealDates = datesWithLoggedMeals
							)
						}
					}
					
					is DataResourceResult.Error -> {
						_uiState.update { it.copy(isLoading = false, isError = true) }
					}
					
					is DataResourceResult.Loading -> {
					}
				}
			}
		}
	}
	
	fun refreshHomeData() {
		viewModelScope.launch {
			dietRepository.getMyBmr("test").collect { bmr ->
				if (bmr != null) {
					_uiState.update {
						it.copy(activityMetabolism = bmr.activityBmr)
					}
				}
			}
		}
		calculateAndupdateTargetPage(_uiState.value.selectedDate, _uiState.value.viewType)
	}
	
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
		if (date == _uiState.value.selectedDate) return
		_uiState.update {
			it.copy(
				selectedDate = date,
				selectedDateInfo = calculateDaysDifference(date)
			)
		}
		calculateAndupdateTargetPage(date, _uiState.value.viewType)
	}
	
	fun onPageScrolled(page: Int) {
		val pageOffset = (page - START_PAGE).toLong()
		val currentViewType = _uiState.value.viewType
		val newDate = if (currentViewType == CalendarViewType.MONTHLY) {
			val originalSelectedDay = _uiState.value.selectedDate.dayOfMonth
			val targetYearMonth = YearMonth.from(LocalDate.now()).plusMonths(pageOffset)
			val lastDayOfTargetMonth = targetYearMonth.lengthOfMonth()
			// 예: 31일 -> 28일 자동 변경, 29일 -> 29일
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
		calculateAndupdateTargetPage(_uiState.value.selectedDate, newViewType)
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
	
//	fun loadInitialData() {
//		viewModelScope.launch {
//			_uiState.update { it.copy(isLoading = true) }
//			try {
//				val dailyExercises = exerciseRepository.getDailyExerciseList()
//				val completedDates = exerciseRepository.getCompletedDailyExerciseDates("test")
//
//				val allExerciseStats = exerciseRepository.getExerciseStat("test")
//				val todayExerciseType = dailyExercises.firstOrNull()
//
//				val todayCount = calculateTodayExerciseCount(allExerciseStats, todayExerciseType)
//
//				_uiState.update {
//					it.copy(
//						isLoading = false,
//						dailyExercise = dailyExercises,
//						dailyExerciseCompletedDates = completedDates, // [추가] 상태 업데이트
//						todayExerciseCount = todayCount
//					)
//				}
//			} catch (e: Exception) {
//				_uiState.update { it.copy(isLoading = false, isError = true) }
//			}
//		}
//	}
	
	private fun calculateTodayExerciseCount(
		stats: List<ExerciseCollection>,
		todayExercise: ExerciseType?
	): Int {
		if (todayExercise == null) return 0
		
		// 전체 통계에서 오늘의 운동에 해당하는 기록을 찾습니다.
		val exerciseForToday = stats.find { it.exerciseType == todayExercise.name }
		
		// "YYYY-MM-DD" 형식의 오늘 날짜 키를 만듭니다.
		val dailyKey = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
		
		// dailyCounts 맵에서 오늘 날짜 키로 횟수를 가져옵니다. 없으면 0을 반환합니다.
		return exerciseForToday?.dailyCounts?.get(dailyKey) ?: 0
	}
	
	
	private fun loadBmrData() {
		viewModelScope.launch {
			dietRepository.getMyBmr("test").collect { bmr ->
				if (bmr != null) {
					_uiState.update {
						it.copy(activityMetabolism = bmr.activityBmr)
					}
				}
			}
		}
	}
	
	private fun loadDietData() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			dietRepository.getAllMeal().collect { result ->
				_uiState.update { it.copy(isLoading = true) }
				when (result) {
					is DataResourceResult.Success -> {
						val meals = result.data
						val today = LocalDate.now()
						val todayMeals = meals[today] ?: emptyList()
						val todayCalories = todayMeals.sumOf { it.kcal }
						val datesWithLoggedMeals = meals.keys
						_uiState.update {
							it.copy(
								isLoading = false,
								isError = false,
								todayTotalCalories = todayCalories,
								loggedMealDates = datesWithLoggedMeals
							)
						}
					}
					
					is DataResourceResult.Error -> {
						_uiState.update {
							it.copy(isLoading = false, isError = true)
						}
					}
					
					is DataResourceResult.Loading -> {
						_uiState.update {
							it.copy(isLoading = true)
						}
					}
				}
			}
		}
	}
}
