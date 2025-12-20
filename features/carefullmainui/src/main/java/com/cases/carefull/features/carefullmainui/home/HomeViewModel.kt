package com.cases.carefull.features.carefullmainui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.common.CalendarViewType
import com.cases.carefull.domain.model.routine.diet.FoodItem
import com.cases.carefull.domain.model.routine.exercise.ExerciseCollection
import com.cases.carefull.domain.model.routine.exercise.ExerciseRecordForDate
import com.cases.carefull.domain.repository.common.CalendarRepository
import com.cases.carefull.domain.repository.routine.diet.DietRecordRepository
import com.cases.carefull.domain.repository.routine.exercise.TodayWorkOutRepository
import com.cases.carefull.domain.repository.routine.exercise.WorkOutRecordRepository
import com.cases.carefull.domain.usecase.routine.diet.BmrUseCases
import com.cases.carefull.domain.usecase.routine.diet.GetSavedBmrUseCase
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.features.carefullmainui.home.HomeUiState.Companion.START_PAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
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
    private val getSavedBmrUseCase: GetSavedBmrUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()
    private val userId = "test"

    private val sharedWorkOutStats = workOutRecordRepository.fetchWorkOutStats(userId)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            replay = 1
        )

    companion object {
        const val TODAY_EXERCISE_GOAL = 30
    }

    init {
        initializeTodayData()
        observeDashboardData()
        observeCalendarChanges()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initializeTodayData() {
        viewModelScope.launch {
            combine(
                getWorkOutFlow(),
                getCaloriesFlow(),
                getBmrFlow()
            ) { workoutResult, caloriesResult, bmrResult ->
                val isLoading = workoutResult is DataResourceResult.Loading ||
                        caloriesResult is DataResourceResult.Loading ||
                        bmrResult is DataResourceResult.Loading

                val isError = workoutResult is DataResourceResult.Error ||
                        caloriesResult is DataResourceResult.Error ||
                        bmrResult is DataResourceResult.Error

                val (exerciseType, count) = if (workoutResult is DataResourceResult.Success) {
                    workoutResult.data
                } else {
                    Pair(null, 0)
                }

                val currentCalories = if (caloriesResult is DataResourceResult.Success) {
                    caloriesResult.data
                } else {
                    0
                }

                val targetBmr = if (bmrResult is DataResourceResult.Success) {
                    bmrResult.data?.tdee ?: 0
                } else {
                    0
                }

                _uiState.value.copy(
                    isLoading = isLoading,
                    isError = isError,
                    todayWorkOut = exerciseType,
                    todayWorkOutCount = count,
                    todayTotalCalories = currentCalories,
                    targetCalories = targetBmr
                )
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getWorkOutFlow() = todayWorkOutRepository.fetchTodayWorkOut()
        .flatMapLatest { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    val exerciseType = result.data
                    todayWorkOutRepository.getTodayWorkOutCount(userId, exerciseType)
                        .map { countResult ->
                            when (countResult) {
                                is DataResourceResult.Success ->
                                    DataResourceResult.Success(
                                        Pair(
                                            exerciseType,
                                            countResult.data
                                        )
                                    )

                                is DataResourceResult.Error ->
                                    DataResourceResult.Error(countResult.exception)

                                is DataResourceResult.Loading ->
                                    DataResourceResult.Loading
                            }
                        }
                }

                is DataResourceResult.Error -> flowOf(DataResourceResult.Error(result.exception))
                is DataResourceResult.Loading -> flowOf(DataResourceResult.Loading)
            }
        }

    private fun getCaloriesFlow() = dietRecordRepository.getMealByDate(LocalDate.now(), userId)
        .map { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    val totalKcal = result.data.sumOf { it.kcal }
                    DataResourceResult.Success(totalKcal)
                }

                is DataResourceResult.Error -> DataResourceResult.Error(result.exception)
                is DataResourceResult.Loading -> DataResourceResult.Loading
            }
        }

    private fun getBmrFlow() = getSavedBmrUseCase(userId)

    private fun observeCalendarChanges() {
        viewModelScope.launch {
            uiState.map { it.selectedDate to it.calendarViewType }
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
                            selectedYearMonth = YearMonth.from(date),
                            selectedDateInfo = calculateDaysDifference(date)
                        )
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeDashboardData() {
        viewModelScope.launch {
            launch {
                _uiState.map { it.selectedYearMonth }
                    .distinctUntilChanged()
                    .flatMapLatest { yearMonth ->
                        combine(
                            dietRecordRepository.getMealsByMonth(yearMonth, userId),
                            sharedWorkOutStats
                        ) { mealsResult, workoutsResult ->

                            processMonthlyMarkingData(mealsResult, workoutsResult)
                        }
                    }
                    .collect { (mealDates, workoutDates) ->
                        _uiState.update {
                            it.copy(
                                dietsRecordDates = mealDates,
                                dailyExerciseCompletedDates = workoutDates
                            )
                        }
                    }
            }
            launch {
                uiState.map { it.selectedDate }
                    .distinctUntilChanged()
                    .onEach {
                        _uiState.update { state -> state.copy(isLoading = true) }
                    }
                    .flatMapLatest { date ->
                        combine(
                            dietRecordRepository.getMealByDate(date, userId),
                            sharedWorkOutStats
                        ) { dietResult, workoutResult ->

                            processDailyDetailData(date, dietResult, workoutResult)
                        }
                    }.collect { (calories, records) ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                selectedDateTotalCalories = calories,
                                selectedDateWorkOutRecords = records
                            )
                        }
                    }
            }
        }
    }

    private fun processMonthlyMarkingData(
        mealsResult: DataResourceResult<Map<LocalDate, List<FoodItem>>>,
        workoutsResult: DataResourceResult<List<ExerciseCollection>>
    ): Pair<Set<LocalDate>, Set<LocalDate>> {
        val mealDates = if (mealsResult is DataResourceResult.Success) {
            mealsResult.data.keys
        } else emptySet()

        val workoutDates = if (workoutsResult is DataResourceResult.Success) {
            workoutsResult.data.flatMap { it.dailyCounts.keys }
                .mapNotNull { dateString ->
                    runCatching {
                        LocalDate.parse(
                            dateString,
                            DateTimeFormatter.ISO_LOCAL_DATE
                        )
                    }.getOrNull()
                }.toSet()
        } else emptySet()

        return Pair(mealDates, workoutDates)
    }

    private fun processDailyDetailData(
        date: LocalDate,
        dietResult: DataResourceResult<List<FoodItem>>,
        workoutResult: DataResourceResult<List<ExerciseCollection>>
    ): Pair<Int, List<ExerciseRecordForDate>> {

        val totalCalories = if (dietResult is DataResourceResult.Success) {
            dietResult.data.sumOf { it.kcal }
        } else {
            if (date == LocalDate.now()) _uiState.value.todayTotalCalories else 0
        }

        val workoutRecords = if (workoutResult is DataResourceResult.Success) {
            val dateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            workoutResult.data.mapNotNull { collection ->
                val count = collection.dailyCounts[dateKey]
                if (count != null && count > 0) {
                    ExerciseRecordForDate(collection.exerciseType.type, count)
                } else null
            }
        } else emptyList()

        return Pair(totalCalories, workoutRecords)
    }

    private fun changeSelectedDate(
        newDate: LocalDate,
        newViewType: CalendarViewType = _uiState.value.calendarViewType
    ) {
        val targetPage = calculateTargetPage(newDate, newViewType)

        _uiState.update {
            it.copy(
                selectedDate = newDate,
                calendarViewType = newViewType,
                pagerTargetPage = targetPage,
                isLoading = true
            )
        }
    }

    private fun calculateTargetPage(
        selectedDate: LocalDate,
        viewType: CalendarViewType
    ): Int {
        return if (viewType == CalendarViewType.MONTHLY) {
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

    fun oneMoreTouchExitToast() {
        viewModelScope.launch {
            _toastEvent.emit("한 번 더 누르면 종료됩니다.")
        }
    }

    fun onDateSelected(date: LocalDate) {
        if (date == _uiState.value.selectedDate) return
        changeSelectedDate(date)
    }

    fun onGoToToday() {
        val today = LocalDate.now()
        if (today == _uiState.value.selectedDate) return
        changeSelectedDate(today)
    }

    fun onToggleViewType() {
        val newViewType = when (_uiState.value.calendarViewType) {
            CalendarViewType.WEEKLY -> CalendarViewType.MONTHLY
            CalendarViewType.MONTHLY -> CalendarViewType.WEEKLY
        }
        changeSelectedDate(_uiState.value.selectedDate, newViewType)
    }

    fun onPageScrolled(page: Int) {
        val pageOffset = (page - START_PAGE).toLong()
        val currentViewType = _uiState.value.calendarViewType

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
                pagerTargetPage = page
            )
        }
    }

    fun onShowYearMonthPicker() {
        _uiState.update { it.copy(isYearMonthPickerVisible = true) }
    }

    fun onHideYearMonthPicker() {
        _uiState.update { it.copy(isYearMonthPickerVisible = false) }
    }

    fun onYearMonthSelected(yearMonth: YearMonth) {
        val newDate = yearMonth.atDay(1)
        _uiState.update { it.copy(isYearMonthPickerVisible = false) }
        changeSelectedDate(newDate, CalendarViewType.MONTHLY)
    }
}

