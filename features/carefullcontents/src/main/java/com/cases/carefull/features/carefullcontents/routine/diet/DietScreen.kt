package com.cases.carefull.features.carefullcontents.routine.diet

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cases.carefull.domain.model.CalendarViewType
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.features.carefullcommon.components.Calendar
import com.cases.carefull.features.carefullcommon.components.CalendarState
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DietRoute(
	viewModel: DietViewModel = hiltViewModel(),
	navController: NavController
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	DietScreen(
		uiState = uiState,
		onDateSelected = viewModel::onDateSelected,
		onShowDatePicker = viewModel::showDatePicker,
		onHideDatePicker = viewModel::hideDatePicker,
		onDatePickerMonthChanged = viewModel::onDatePickerMonthChanged,
		onGoToToday = viewModel::onGoToToday,
		onAddMealClick = { mealType ->
			navController.navigate(RoutineRoute.DietSearchScreen(mealType = mealType.name))
		},
		onRemoveMealClick = viewModel::onRemoveMeal
	)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DietScreen(
	uiState: DietUiState,
	onDateSelected: (LocalDate) -> Unit,
	onShowDatePicker: () -> Unit,
	onHideDatePicker: () -> Unit,
	onDatePickerMonthChanged: (Long) -> Unit,
	onGoToToday: () -> Unit,
	onAddMealClick: (MealType) -> Unit,
	onRemoveMealClick: (DietCollection) -> Unit
) {
	val lazyListState = rememberLazyListState()

	LaunchedEffect(uiState.selectedDate) {
		val targetIndex = uiState.dietSections.indexOfFirst { it.date == uiState.selectedDate }
		if (targetIndex != -1) {
			lazyListState.scrollToItem(index = targetIndex)
		}
	}

	if (uiState.isDatePickerVisible) {
		val calendarState = CalendarState(
			selectedDate = uiState.selectedDate,
			displayedYearMonth = uiState.datePickerDisplayedMonth,
			viewType = CalendarViewType.MONTHLY,
			calendarDates = uiState.datePickerCalendarDates,
			selectedDateInfo = "",
			markedDates = uiState.allMealLoggedDates
		)
		DatePickerDialog(
			calendarState = calendarState,
			onDateSelected = {
				onDateSelected(it)
				onHideDatePicker()
			},
			onDismiss = onHideDatePicker,
			onMonthChanged = onDatePickerMonthChanged,
			onGoToToday = onGoToToday
		)
	}

	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		state = lazyListState
	) {
		item {
			NutritionSummary(uiState = uiState)
		}

		item {
			val section = uiState.selectedDateSection

			DateHeader(
				date = uiState.selectedDate,
				totalCalories = section?.totalCalories ?: 0,
				onCalendarClick = onShowDatePicker
			)
			HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

			Column(modifier = Modifier.padding(horizontal = 16.dp)) {
				val mealsByTimeForDay = section?.meals?.groupBy {
					try { MealType.valueOf(it.mealType) } catch (e: IllegalArgumentException) { MealType.SNACK }
				} ?: emptyMap()

				MealType.entries.forEach { mealType ->
					val addedFoodsForMealType = mealsByTimeForDay[mealType] ?: emptyList()
					MealSection(
						mealType = mealType,
						addedFoods = addedFoodsForMealType,
//						onCameraClick = {},
						onAddClick = { onAddMealClick(mealType) },
						onRemoveClick = onRemoveMealClick
					)
				}
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(
	calendarState: CalendarState,
	onDateSelected: (LocalDate) -> Unit,
	onDismiss: () -> Unit,
	onMonthChanged: (Long) -> Unit,
	onGoToToday: () -> Unit
) {
	val startPage = Int.MAX_VALUE / 2
	val pagerState = rememberPagerState(
		initialPage = startPage,
		pageCount = { Int.MAX_VALUE })
	var previousPage by remember { mutableIntStateOf(startPage) }
	
	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.currentPage }
			.distinctUntilChanged()
			.collect { currentPage ->
				val monthDifference = (currentPage - previousPage).toLong()
				if (monthDifference != 0L) {
					onMonthChanged(monthDifference)
				}
				previousPage = currentPage
			}
	}
	
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("날짜 선택") },
		text = {
			Calendar(
				calendarState = calendarState,
				pagerState = pagerState,
				onDateClick = { date ->
					onDateSelected(date)
				},
				onToggleViewType = {},
				onMonthPickerClick = {},
				onGoToToday = { onGoToToday() }
			)
		},
		confirmButton = {
			TextButton(onClick = onDismiss) {
				Text("닫기")
			}
		}
	)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionSummary(uiState: DietUiState) {
	Column {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.Center
		) {
			Text(
				text = "탄수화물 : ${uiState.totalCarbs}g",
				style = MaterialTheme.typography.bodyLarge,
				modifier = Modifier.padding(horizontal = 16.dp)
			)
			Text(
				text = "단백질 : ${uiState.totalProteins}g",
				style = MaterialTheme.typography.bodyLarge,
				modifier = Modifier.padding(horizontal = 16.dp)
			)
			Text(
				text = "지방 : ${uiState.totalFats}g",
				style = MaterialTheme.typography.bodyLarge,
				modifier = Modifier.padding(horizontal = 16.dp)
			)
		}
		HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
	}
}

@Composable
fun MealSection(
	mealType: MealType,
	addedFoods: List<DietCollection>,
//	onCameraClick: () -> Unit,
	onAddClick: () -> Unit,
	onRemoveClick: (DietCollection) -> Unit
) {
	Card(
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		colors = CardDefaults.cardColors(
			containerColor = Color.White
		)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = " ${mealType.time}",
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Bold
				)
				Spacer(modifier = Modifier.weight(1f))
//				IconButton(onClick = onCameraClick) {
//					Icon(
//						imageVector = Icons.Default.Camera,
//						contentDescription = "${mealType.time} 음식 추가",
//						tint = MaterialTheme.colorScheme.primary,
//						modifier = Modifier.size(28.dp)
//					)
//				}
				IconButton(onClick = onAddClick) {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = "${mealType.time} 음식 검색",
						tint = MaterialTheme.colorScheme.primary,
						modifier = Modifier.size(20.dp)
					)
				}
			}
			HorizontalDivider(
				thickness = 1.dp,
				color = MaterialTheme.colorScheme.outline,
				modifier = Modifier.padding(bottom = 4.dp)
			)

			if (addedFoods.isNotEmpty()) {
				Column(
					modifier = Modifier.fillMaxWidth(),
					verticalArrangement = Arrangement.spacedBy(4.dp)
				) {
					addedFoods.forEach { addedFood ->
						FoodItemRow(
							food = addedFood,
							onRemove = { onRemoveClick(addedFood) }
						)
					}
				}
			} else {
				Column(
					modifier = Modifier.fillMaxWidth()
						.padding(bottom = 8.dp)
				) {
					Text(
						text = "   아직 추가된 음식이 없습니다.",
						style = MaterialTheme.typography.bodyMedium,
						color = Color.Gray
					)
				}
			}
		}
	}
}

@Composable
fun FoodItemRow(
	food: DietCollection,
	onRemove: () -> Unit
) {
	Column(
		modifier = Modifier.padding(bottom = 8.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = "${food.mealName} (${food.weight}g)",
				style = MaterialTheme.typography.bodyMedium
			)
			Spacer(modifier = Modifier.weight(1f))
			Text(text = "${food.kcal} kcal  ", style = MaterialTheme.typography.bodySmall)
			
			IconButton(onClick = onRemove, modifier = Modifier.size(16.dp)) {
				Icon(imageVector = Icons.Default.Close, contentDescription = "삭제")
			}
			Spacer(modifier = Modifier.width(5.dp))
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader(
	date: LocalDate,
	totalCalories: Int,
	onCalendarClick: () -> Unit
) {
	Surface(
		color = Color.White
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			IconButton(onClick = onCalendarClick) {
				Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "날짜 선택")
			}
			Spacer(modifier = Modifier.width(16.dp))
			Text(
				text = formatDate(date),
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.weight(1f)
			)
			Text(
				text = "총 $totalCalories kcal",
				style = MaterialTheme.typography.bodyMedium
			)
			
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(date: LocalDate): String {
	val today = LocalDate.now()
	val yesterday = today.minusDays(1)
	
	return when {
		date.isEqual(today) -> "오늘"
		date.isEqual(yesterday) -> "어제"
		else -> {
			date.format(DateTimeFormatter.ofPattern("M월 d일"))
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DietScreenPreview() {
	val fakeUiState = DietUiState(
		selectedDate = LocalDate.now(),
		totalCalories = 1250,
		totalCarbs = 150,
		totalProteins = 80,
		totalFats = 45,
		selectedDateSection = DietDateSection(
			date = LocalDate.now(),
			meals = listOf(
				DietCollection(mealName = "닭가슴살", weight = 100, kcal = 110, mealType = MealType.LUNCH.name),
				DietCollection(mealName = "현미밥", weight = 210, kcal = 350, mealType = MealType.LUNCH.name),
				DietCollection(mealName = "프로틴 쉐이크", weight = 30, kcal = 120, mealType = MealType.SNACK.name)
			),
			totalCalories = 580,
		),
	)

	CarefullTheme {
		DietScreen(
			uiState = fakeUiState,
			onDateSelected = {},
			onShowDatePicker = {},
			onHideDatePicker = {},
			onDatePickerMonthChanged = {},
			onGoToToday = {},
			onAddMealClick = {},
			onRemoveMealClick = {}
		)
	}
}