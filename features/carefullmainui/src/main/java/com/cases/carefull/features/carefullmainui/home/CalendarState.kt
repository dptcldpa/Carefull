package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.cases.carefull.domain.model.CalendarViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters



@RequiresApi(Build.VERSION_CODES.O)
data class CalendarState(
	val pagerState: PagerState,
	private val scope: CoroutineScope
) {
	var viewType by mutableStateOf(CalendarViewType.WEEKLY)
	var selectedDate: LocalDate by mutableStateOf(LocalDate.now())
	val currentDate: LocalDate = LocalDate.now()
	var isProgrammaticScroll by mutableStateOf(false)
	var isYearMonthPickerVisible by mutableStateOf(false)
	val currentPageYearMonth: YearMonth
		get() {
			return if (viewType == CalendarViewType.MONTHLY) {
				val monthOffset = pagerState.currentPage - START_PAGE_MONTH
				YearMonth.from(currentDate).plusMonths(monthOffset.toLong())
			} else {
				YearMonth.from(selectedDate)
			}
		}
	
	fun goToToday() {
		scope.launch {
			isProgrammaticScroll = true
			selectedDate = currentDate
			val targetPage = when (viewType) {
				CalendarViewType.MONTHLY -> {
					val startOfMonth = YearMonth.from(currentDate)
					val monthDiff =
						ChronoUnit.MONTHS.between(startOfMonth, YearMonth.from(selectedDate))
					START_PAGE_MONTH + monthDiff.toInt()
				}
				
				CalendarViewType.WEEKLY -> {
					val startOfReferenceWeek =
						currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
					val startOfSelectedWeek =
						selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
					val weekDiff =
						ChronoUnit.WEEKS.between(startOfReferenceWeek, startOfSelectedWeek)
					START_PAGE_WEEK + weekDiff.toInt()
				}
			}
			pagerState.animateScrollToPage(targetPage)
			isProgrammaticScroll = false
		}
	}
	
	fun onYearMonthSelected(yearMonth: YearMonth) {
		selectedDate = yearMonth.atDay(1)
		viewType = CalendarViewType.MONTHLY
		isYearMonthPickerVisible = false
		
		scope.launch {
			val monthDiff = ChronoUnit.MONTHS.between(
				YearMonth.from(currentDate),
				yearMonth
			)
			val targetPage = START_PAGE_MONTH + monthDiff.toInt()
			pagerState.animateScrollToPage(targetPage)
		}
	}
	
	
	fun getDaysOfWeek(date: LocalDate): List<LocalDate> {
		val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
		return generateSequence(startOfWeek) { it.plusDays(1) }.take(7).toList()
	}
	
	
	// 페이지에 표시할 날짜들을 계산하는 함수(연월을 입력받음)
	fun getDaysOfMonth(yearMonth: YearMonth): List<LocalDate> {
		// 입력받은 연월의 1일을 토대로 시작일 계산(시작점을 일요일로 설정)
		val startOfMonth =
			yearMonth.atDay(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
		// 입력받은 연월의 마지막 날을 토대로 종료일 계산(종료점을 토요일로 설정)
		val endOfMonth =
			yearMonth.atEndOfMonth().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
		// 하루씩 증가하는 날짜 시퀀스 생성(종료점까지) 후 리스트화
		return generateSequence(startOfMonth) { it.plusDays(1) }
			.takeWhile { !it.isAfter(endOfMonth) }
			.toList()
	}
	
	fun updateViewType(newViewType: CalendarViewType) {
		if (newViewType == viewType) return
		
		scope.launch {
			viewType = newViewType // 먼저 뷰 타입을 변경
			
			if (newViewType == CalendarViewType.MONTHLY) {
				// 월간 뷰로 전환: 선택된 날짜가 포함된 '월' 페이지로 이동
				val targetPage = START_PAGE_MONTH + ChronoUnit.MONTHS.between(
					YearMonth.from(currentDate),
					YearMonth.from(selectedDate)
				).toInt()
				pagerState.scrollToPage(targetPage)
			} else { // WEEKLY
				// 1. 기준이 되는 주의 시작일(일요일)을 구함
				val startOfReferenceWeek =
					currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
				// 2. 선택된 날짜가 속한 주의 시작일(일요일)을 구함
				val startOfSelectedWeek =
					selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
				// 3. 두 주의 시작일 사이의 주(week) 차이를 계산
				val weekDifference =
					ChronoUnit.WEEKS.between(startOfReferenceWeek, startOfSelectedWeek)
				val targetPage = START_PAGE_WEEK + weekDifference.toInt()
				pagerState.scrollToPage(targetPage)
			}
		}
	}
	
	// 날짜 차이 계산
	fun calculateDaysDifference(): String {
		val daysDifference = selectedDate.toEpochDay() - currentDate.toEpochDay()
		return when {
			daysDifference == 0L -> "오늘"
			daysDifference == 1L -> "내일"
			daysDifference == -1L -> "어제"
			daysDifference > 0 -> "${daysDifference}일 후"
			else -> "${-daysDifference}일 전"
		}
	}
	
	// 화면 이동 시에도 정보가 유지되도록 세이버 설정
	companion object {
		const val START_PAGE_MONTH = Int.MAX_VALUE / 2
		const val START_PAGE_WEEK = Int.MAX_VALUE / 2
		fun Saver(pagerState: PagerState, scope: CoroutineScope): Saver<CalendarState, Any> =
			listSaver(
				save = {
					listOf(
						it.viewType.name,
						it.selectedDate.toEpochDay()
					)
				},
				restore = { savedValue ->
					CalendarState(pagerState, scope).apply {
						viewType = CalendarViewType.valueOf(savedValue[0] as String)
						selectedDate = LocalDate.ofEpochDay(savedValue[1] as Long)
					}
				}
			)
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberCalendarState(
	scope: CoroutineScope = rememberCoroutineScope()
): CalendarState {
	val pagerState = rememberPagerState(
		initialPage = CalendarState.START_PAGE_WEEK,
		pageCount = { Int.MAX_VALUE }
	)
	return rememberSaveable(
		inputs = arrayOf(pagerState, scope),
		saver = CalendarState.Saver(pagerState, scope),
		init = {
			CalendarState(
				pagerState = pagerState,
				scope = scope
			)
		}
	)
}