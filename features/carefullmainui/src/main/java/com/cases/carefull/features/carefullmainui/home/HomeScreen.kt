package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cases.carefull.domain.model.CalendarViewType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
	viewModel: HomeViewModel
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val pagerState = rememberPagerState(
		initialPage = HomeViewModel.START_PAGE,
		pageCount = { Int.MAX_VALUE }
	)
	LaunchedEffect(pagerState.settledPage) {
		if (pagerState.settledPage != HomeViewModel.START_PAGE) {
			viewModel.onPageScrolled(pagerState.settledPage)
		}
	}
	LaunchedEffect(uiState.selectedDate, uiState.viewType) {
		val targetPage = if (uiState.viewType == CalendarViewType.MONTHLY) {
			HomeViewModel.START_PAGE + ChronoUnit.MONTHS.between(
				YearMonth.from(LocalDate.now()),
				YearMonth.from(uiState.selectedDate)
			).toInt()
		} else { // WEEKLY
			// 1. 현재 Pager 페이지가 보여주는 주의 시작일 계산
			val pageOffset = pagerState.currentPage - HomeViewModel.START_PAGE
			val startOfCurrentPageWeek = LocalDate.now()
				.plusWeeks(pageOffset.toLong())
				.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
			
			// 2. 선택된 날짜가 속한 주의 시작일 계산
			val startOfSelectedDateWeek = uiState.selectedDate
				.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
			
			if (startOfCurrentPageWeek != startOfSelectedDateWeek) {
				val startOfReferenceWeek =
					LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
				HomeViewModel.START_PAGE + ChronoUnit.WEEKS.between(
					startOfReferenceWeek,
					startOfSelectedDateWeek
				).toInt()
			} else {
				pagerState.currentPage
			}
		}
		
		if (pagerState.currentPage != targetPage) {
			pagerState.animateScrollToPage(targetPage)
		}
	}
	Column(
		modifier = Modifier
			.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Calendar(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 8.dp),
			uiState = uiState,
			pagerState = pagerState,
			onDateClick = { date ->
				viewModel.onDateSelected(date)
			},
			onToggleViewType = {
				viewModel.onToggleViewType()
			},
			onMonthPickerClick = {
				viewModel.showYearMonthPicker()
			},
			onGoToToday = {
				viewModel.onGoToToday()
			}
		)
		Spacer(modifier = Modifier.weight(1f))
		Text(
			text = "선택된 날짜 정보",
			style = MaterialTheme.typography.titleMedium
		)
		Text(
			text = "날짜: ${uiState.selectedDate}",
			style = MaterialTheme.typography.titleMedium
		)
		Text(
			text = "상태: ${uiState.selectedDateInfo}",
			style = MaterialTheme.typography.titleMedium
		)
	}
	YearMonthPickerDialog(
		isVisible = uiState.isYearMonthPickerVisible,
		initialYearMonth = uiState.displayedYearMonth,
		onDismissRequest = {
			viewModel.hideYearMonthPicker()
		},
		onYearMonthSelected = { selectedYearMonth ->
			viewModel.onYearMonthSelected(selectedYearMonth)
		}
	)
}
