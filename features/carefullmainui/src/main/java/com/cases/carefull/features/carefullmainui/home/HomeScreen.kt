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
import androidx.compose.material3.CardDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cases.carefull.domain.model.CalendarViewType
import com.cases.carefull.features.carefullcommon.components.Calendar
import com.cases.carefull.features.carefullcommon.components.CalendarState
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullmainui.home.HomeUiState.Companion.START_PAGE
import kotlinx.coroutines.flow.distinctUntilChanged

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
	viewModel: HomeViewModel = hiltViewModel(),
	navController: NavController
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	
	val todayExercise = uiState.dailyExercise.firstOrNull()
	
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
		markedDates = uiState.loggedMealDates,
		dailyExerciseCompletedDates = uiState.dailyExerciseCompletedDates
	)
	
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
			},
			calendarFooterContent = {
				val shouldShowDetails = uiState.viewType == CalendarViewType.MONTHLY &&
						(uiState.selectedDateExerciseRecords.isNotEmpty() || uiState.selectedDateTotalCalories > 0)
				
				if (shouldShowDetails) {
					if (uiState.selectedDateExerciseRecords.isNotEmpty()) {
						Column {
							Text(
								"운동 기록",
								fontWeight = FontWeight.Bold,
								style = MaterialTheme.typography.titleSmall
							)
							Spacer(modifier = Modifier.height(4.dp))
							uiState.selectedDateExerciseRecords.forEach { record ->
								Row(
									modifier = Modifier.fillMaxWidth(),
									horizontalArrangement = Arrangement.SpaceBetween,
									verticalAlignment = Alignment.CenterVertically
								) {
									Text(
										record.name,
										style = MaterialTheme.typography.bodyMedium
									)
									Text(
										"${record.count}회",
										style = MaterialTheme.typography.bodyMedium,
										color = Color.Gray
									)
								}
							}
						}
					}
					
					
					// 식단 기록 섹션
					if (uiState.selectedDateTotalCalories > 0) {
						Column {
							Text(
								"식단 기록",
								fontWeight = FontWeight.Bold,
								style = MaterialTheme.typography.titleSmall
							)
							Spacer(modifier = Modifier.height(4.dp))
							Row(
								modifier = Modifier.fillMaxWidth(),
								horizontalArrangement = Arrangement.SpaceBetween,
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									"총 섭취 칼로리",
									style = MaterialTheme.typography.bodyMedium
								)
								Text(
									"${uiState.selectedDateTotalCalories} kcal",
									style = MaterialTheme.typography.bodyMedium,
									color = Color.Gray
								)
							}
						}
					}
				}
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
				if (todayExercise != null) {
					WorkoutInfoCard(
						exerciseName = todayExercise.type,
						todayCount = uiState.todayExerciseCount,
						goalCount = HomeViewModel.TODAY_EXERCISE_GOAL,
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
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = Color.White
		)
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
	todayCount: Int,
	goalCount: Int,
	onClick: () -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = Color.White
		)
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
				Row(
					verticalAlignment = Alignment.Bottom,
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Text(
						text = exerciseName,
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.Bold
					)
					Text(
						text = buildAnnotatedString {
							withStyle(
								style = SpanStyle(
									fontWeight = FontWeight.Bold,
									fontSize = 22.sp
								)
							) {
								append("$todayCount")
							}
							append(" / $goalCount 회")
						},
						style = MaterialTheme.typography.bodyLarge
					)
				}
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

