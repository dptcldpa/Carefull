package com.cases.carefull.features.carefullmainui.home

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.cases.carefull.domain.model.exercise.ExerciseRecordForDate
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.calendar.Calendar
import com.cases.carefull.features.carefullcommon.calendar.CalendarState
import com.cases.carefull.features.carefullcommon.components.ComposableToast
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullmainui.home.HomeUiState.Companion.START_PAGE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(
        initialPage = START_PAGE,
        pageCount = { Int.MAX_VALUE }
    )

    val calendarState = CalendarState(
        selectedDate = uiState.selectedDate,
        displayedYearMonth = uiState.selectedYearMonth,
        viewType = uiState.calendarViewType,
        calendarDates = uiState.calendarDates,
        selectedDateInfo = uiState.selectedDateInfo,
        dietsRecordDates = uiState.dietsRecordDates,
        dailyExerciseCompletedDates = uiState.dailyExerciseCompletedDates
    )

    val context = LocalContext.current
    val activity = (context as? Activity)
    val scope = rememberCoroutineScope()
    var backPressedOnce by remember { mutableStateOf(false) }

    ComposableToast(toastEvent = viewModel.toastEvent)

    BackHandler(enabled = true) {
        if (backPressedOnce) {
            activity?.finish()
        } else {
            backPressedOnce = true
            viewModel.oneMoreTouchExitToast()
            scope.launch {
                delay(2000L)
                backPressedOnce = false
            }
        }
    }
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
        initialYearMonth = uiState.selectedYearMonth,
        onDismissRequest = {
            viewModel.onHideYearMonthPicker()
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
                viewModel.onShowYearMonthPicker()
            },
            onGoToToday = {
                viewModel.onGoToToday()
            },
            calendarFooterContent = {
                SelectedDateDetailSection(
                    viewType = uiState.calendarViewType,
                    workoutRecords = uiState.selectedDateWorkOutRecords,
                    totalCalories = uiState.selectedDateTotalCalories
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DietInfoCard(
                    todayCalories = uiState.todayTotalCalories,
                    targetCalories = uiState.targetCalories,
                    onClick = { navController.navigate(RoutineRoute.DietRoute) }
                )
                if (uiState.todayWorkOut != null) {
                    WorkoutInfoCard(
                        workOutName = uiState.todayWorkOut!!.type,
                        todayCount = uiState.todayWorkOutCount,
                        goalCount = HomeViewModel.TODAY_EXERCISE_GOAL,
                        onClick = {
                            navController.navigate(
                                RoutineRoute.WorkOutRoute(
                                    exerciseType = uiState.todayWorkOut!!
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedDateDetailSection(
    viewType: CalendarViewType,
    workoutRecords: List<ExerciseRecordForDate>,
    totalCalories: Int
) {
    val shouldShowDetails = viewType == CalendarViewType.MONTHLY &&
            (workoutRecords.isNotEmpty() || totalCalories > 0)

    if (shouldShowDetails) {
        if (workoutRecords.isNotEmpty()) {
            Column {
                Text(
                    stringResource(R.string.record_workout_title),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                workoutRecords.forEach { record ->
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
                            stringResource(R.string.unit_count_format, record.count),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        if (totalCalories > 0) {
            Column {
                Text(
                    stringResource(R.string.record_diet_title),
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
                        stringResource(R.string.record_total_calorie_intake),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.unit_kcal_format, totalCalories),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
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
                    text = stringResource(R.string.record_today_calorie_intake),
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
                        append(stringResource(R.string.calorie_goal_format, targetCalories))
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = stringResource(R.string.record_button_add_diet),
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutInfoCard(
    workOutName: String,
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
                    text = stringResource(R.string.record_today_workout),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = workOutName,
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
                            append(stringResource(R.string.count_goal_format, goalCount))
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = stringResource(R.string.workout_button_start),
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
        }
    }
}
