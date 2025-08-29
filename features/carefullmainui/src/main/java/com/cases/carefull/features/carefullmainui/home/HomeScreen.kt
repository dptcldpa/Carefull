package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
	viewModel: HomeViewModel
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val calendarState = rememberCalendarState()
	
	Column(
		modifier = Modifier
			.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Calendar(
			modifier = Modifier
				.fillMaxWidth(),
			state = calendarState,
			onClick = {
				viewModel.onDateSelected(
					date = calendarState.selectedDate,
					dateDifference = calendarState.calculateDaysDifference()
				)
			},
			onMonthPickerClick = {
				calendarState.isYearMonthPickerVisible = true
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
		isVisible = calendarState.isYearMonthPickerVisible,
		initialYearMonth = calendarState.currentPageYearMonth,
		onDismissRequest = {
			calendarState.isYearMonthPickerVisible = false
		},
		onYearMonthSelected = { selectedYearMonth ->
			calendarState.onYearMonthSelected(selectedYearMonth)
		}
	)
}