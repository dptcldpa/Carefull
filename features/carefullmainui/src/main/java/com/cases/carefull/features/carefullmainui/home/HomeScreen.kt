package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cases.carefull.features.carefullcommon.components.Calendar
import com.cases.carefull.features.carefullcommon.components.CalendarState
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullmainui.home.HomeUiState.Companion.START_PAGE
import kotlinx.coroutines.flow.distinctUntilChanged

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
	viewModel: HomeViewModel,
	navController: NavController
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val pagerState = rememberPagerState(
		initialPage = START_PAGE,
		pageCount = { Int.MAX_VALUE }
	)
	val calendarState = CalendarState(
		selectedDate = uiState.selectedDate,
		displayedYearMonth = uiState.displayedYearMonth,
		viewType = uiState.viewType,
		calendarDates = uiState.calendarDates,
		selectedDateInfo = uiState.selectedDateInfo,
		markedDates = uiState.loggedMealDates
	)
	val todayExerciseName = uiState.dailyExercise.first().type
	
	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.settledPage }
			.distinctUntilChanged()
			.collect { page ->
				viewModel.onPageScrolled(page)
			}
	}
	
	LaunchedEffect(uiState.pagerTargetPage) {
		if (pagerState.currentPage != uiState.pagerTargetPage) {
			pagerState.animateScrollToPage(uiState.pagerTargetPage)
		}
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
	
	Column(
		modifier = Modifier.fillMaxSize()
	) {
		Calendar(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 8.dp),
			calendarState = calendarState,
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
		
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp, vertical = 8.dp),
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				DietInfoCard(
					todayCalories = uiState.todayTotalCalories,
					targetCalories = uiState.activityMetabolism,
					onClick = { navController.navigate(RoutineRoute.DietScreen) }
				)
				WorkoutInfoCard(
					exerciseName = todayExerciseName,
					onClick = {
						navController.navigate(
							RoutineRoute.WorkOutScreen(
								exerciseType = uiState.dailyExercise.first(),
								count = 10
							)
						)
					}
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietInfoCard(
	todayCalories: Int,
	targetCalories: Int,
	onClick: () -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		border = BorderStroke(1.dp, Color.LightGray),
		onClick = onClick
	) {
		Row(
			modifier = Modifier
				.padding(horizontal = 20.dp, vertical = 24.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = "오늘의 섭취 칼로리",
					style = MaterialTheme.typography.titleMedium,
					color = Color.Gray
				)
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = buildAnnotatedString {
						withStyle(
							style = SpanStyle(
								fontWeight = FontWeight.Bold,
								fontSize = 22.sp
							)
						) {
							append("$todayCalories")
						}
						append(" / $targetCalories kcal")
					},
					style = MaterialTheme.typography.bodyLarge
				)
			}
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
				contentDescription = "식단 입력",
				modifier = Modifier.size(20.dp),
				tint = Color.Gray
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutInfoCard(
	exerciseName: String,
	onClick: () -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		border = BorderStroke(1.dp, Color.LightGray),
		onClick = onClick
	) {
		Row(
			modifier = Modifier
				.padding(horizontal = 20.dp, vertical = 24.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = "오늘의 운동",
					style = MaterialTheme.typography.titleMedium,
					color = Color.Gray
				)
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = exerciseName,
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Bold
				)
			}
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
				contentDescription = "운동 시작",
				modifier = Modifier.size(20.dp),
				tint = Color.Gray
			)
		}
	}
}

