package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.CalendarViewType
import com.cases.carefull.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
	private val homeRepository: HomeRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState = _uiState.asStateFlow()
	
	companion object {
		const val START_PAGE = Int.MAX_VALUE / 2
	}
	
	init {
		updateCalendarDates(_uiState.value.selectedDate)
		viewModelScope.launch {
			uiState.map { it.selectedDate }
				.distinctUntilChanged()
				.collect { date ->
					updateCalendarDates(date, uiState.value.viewType)
				}
		}
	}
	
	private fun updateCalendarDates(
		date: LocalDate,
		viewType: CalendarViewType = _uiState.value.viewType
	) {
		val dates = if (viewType == CalendarViewType.MONTHLY) {
			homeRepository.getDaysOfMonth(YearMonth.from(date))
		} else {
			homeRepository.getDaysOfWeek(date)
		}
		_uiState.update {
			it.copy(
				calendarDates = dates,
				displayedYearMonth = YearMonth.from(date)
			)
		}
	}
	
	fun onDateSelected(date: LocalDate) {
		_uiState.update {
			it.copy(
				selectedDate = date,
				selectedDateInfo = calculateDaysDifference(date)
			)
		}
	}
	
	fun onPageScrolled(page: Int) {
		val pageOffset = page - START_PAGE
		val newDate = if (_uiState.value.viewType == CalendarViewType.MONTHLY) {
			LocalDate.now().plusMonths(pageOffset.toLong()).withDayOfMonth(1)
		} else {
			LocalDate.now().plusWeeks(pageOffset.toLong())
		}
		_uiState.update {
			it.copy(
				displayedYearMonth = YearMonth.from(newDate),
				selectedDate = if (it.viewType == CalendarViewType.MONTHLY) newDate else it.selectedDate,
				selectedDateInfo = calculateDaysDifference(if (it.viewType == CalendarViewType.MONTHLY) newDate else it.selectedDate)
			)
		}
		updateCalendarDates(newDate, _uiState.value.viewType)
	}
	
	fun onToggleViewType() {
		val newViewType = if (_uiState.value.viewType == CalendarViewType.WEEKLY) {
			CalendarViewType.MONTHLY
		} else {
			CalendarViewType.WEEKLY
		}
		_uiState.update { it.copy(viewType = newViewType) }
		updateCalendarDates(_uiState.value.selectedDate, newViewType)
	}
	
	fun onGoToToday() {
		onDateSelected(LocalDate.now())
	}
	
	fun showYearMonthPicker() {
		_uiState.update { it.copy(isYearMonthPickerVisible = true) }
	}
	
	fun hideYearMonthPicker() {
		_uiState.update { it.copy(isYearMonthPickerVisible = false) }
	}
	
	// [이동] 년/월 선택 완료 이벤트 처리
	fun onYearMonthSelected(yearMonth: YearMonth) {
		_uiState.update {
			it.copy(
				selectedDate = yearMonth.atDay(1),
				viewType = CalendarViewType.MONTHLY,
				isYearMonthPickerVisible = false,
				selectedDateInfo = calculateDaysDifference(yearMonth.atDay(1))
			)
		}
	}
	
	// [이동] 날짜 차이 계산 로직 (private 함수로 변경)
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
}
