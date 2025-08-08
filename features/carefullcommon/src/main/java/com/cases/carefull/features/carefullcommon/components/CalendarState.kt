package com.cases.carefull.features.carefullcommon.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters


enum class CalendarSize {
	HALF, FULL
}

@RequiresApi(Build.VERSION_CODES.O)
data class CalendarState(
	val datePagerState: PagerState
) {
	var snapState by mutableStateOf(CalendarSize.FULL)
	var selectedDate: LocalDate by mutableStateOf(LocalDate.now())
	val currentDate: LocalDate // 연산 프로퍼티
		@RequiresApi(Build.VERSION_CODES.O)
		get() =  LocalDate.now()
	val currentPageYM: YearMonth // 연산 프로퍼티
		get() = YearMonth.from(currentDate).plusMonths((datePagerState.currentPage - Int.MAX_VALUE / 2).toLong())
	
	// 페이지에 표시할 날짜들을 계산하는 함수(연월을 입력받음)
	fun getDaysOfMonth(yearMonth: YearMonth): List<LocalDate> {
		// 입력받은 연월의 1일을 토대로 시작일 계산(시작점을 일요일로 설정)
		val startOfMonth = yearMonth.atDay(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
		// 입력받은 연월의 마지막 날을 토대로 종료일 계산(종료점을 토요일로 설정)
		val endOfMonth = yearMonth.atEndOfMonth().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
		// 하루씩 증가하는 날짜 시퀀스 생성(종료점까지) 후 리스트화
		return generateSequence(startOfMonth) { it.plusDays(1) }
			.takeWhile { !it.isAfter(endOfMonth) }
			.toList()
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
		fun Saver(
			datePagerState: PagerState
		): Saver<CalendarState, Any> = listSaver(
			save = {
				listOf(
					it.snapState,
					it.selectedDate
				)
			},
			restore = { savedValue ->
				CalendarState(
					datePagerState = datePagerState
				).apply {
					snapState = savedValue[0] as CalendarSize
					selectedDate = savedValue[1] as LocalDate
				}
			}
		)
	}
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberCalendarState(
	// 무한 스크롤이 가능하도록 설정
	datePagerState: PagerState = rememberPagerState(Int.MAX_VALUE / 2) { Int.MAX_VALUE },
): CalendarState {
	return rememberSaveable(
		datePagerState,
		saver = CalendarState.Saver(datePagerState)
	) {
		CalendarState(
			datePagerState = datePagerState
		)
	}
}
