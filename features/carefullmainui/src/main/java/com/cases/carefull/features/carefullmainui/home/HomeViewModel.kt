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
import com.cases.carefull.features.carefullmainui.home.HomeUiState.Companion.START_PAGE
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

    init {
        observeCalendarChanges()
        observeAllMeals()
        observeActivityBmr()
        observeDailyExercise()
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

    fun observeActivityBmr() {
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

    fun observeDailyExercise() {
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
}
