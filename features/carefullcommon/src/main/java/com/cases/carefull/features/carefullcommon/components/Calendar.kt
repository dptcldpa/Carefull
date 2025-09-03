package com.cases.carefull.features.carefullcommon.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cases.carefull.domain.model.CalendarViewType
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
	modifier: Modifier = Modifier,
	calendarState: CalendarState,
	pagerState: PagerState,
	onDateClick: (LocalDate) -> Unit,
	onToggleViewType: () -> Unit,
	onMonthPickerClick: () -> Unit,
	onGoToToday: () -> Unit
) {
	var totalDragY by remember { mutableFloatStateOf(0f) }
	val swipeThreshold = with(LocalDensity.current) { 50.dp.toPx() }
	Surface(
		modifier = modifier
			.fillMaxWidth()
			.pointerInput(calendarState.viewType) {
				detectDragGestures(
					onDragStart = { totalDragY = 0f },
					onDragEnd = {
						if (totalDragY > swipeThreshold && calendarState.viewType == CalendarViewType.WEEKLY) {
							onToggleViewType()
						} else if (totalDragY < -swipeThreshold && calendarState.viewType == CalendarViewType.MONTHLY) {
							onToggleViewType()
						}
					}
				) { change, dragAmount ->
					val (x, y) = dragAmount
					if (abs(y) > abs(x)) {
						change.consume()
						totalDragY += y
					}
				}
			},
		shape = RoundedCornerShape(16.dp),
		border = BorderStroke(1.dp, Color.LightGray),
		color = Color.White
	) {
		Column(modifier = Modifier.animateContentSize()) {
			CalendarHeader(
				calendarState = calendarState,
				onToggleViewType = onToggleViewType,
				onMonthPickerClick = onMonthPickerClick,
				onGoToToday = onGoToToday
			)
			WeekDays()
			CalendarGrid(
				calendarState = calendarState,
				pagerState = pagerState,
				onDateClick = onDateClick
			)
			CalendarFooter(
				selectedDateInfo = calendarState.selectedDateInfo
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarHeader(
	calendarState: CalendarState,
	onToggleViewType: () -> Unit,
	onMonthPickerClick: () -> Unit,
	onGoToToday: () -> Unit
) {
	val targetBias = if (calendarState.viewType == CalendarViewType.MONTHLY) 0f else -1f
	val animatedBias by animateFloatAsState(
		targetValue = targetBias,
		animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
		label = "Header Alignment Bias"
	)
	val targetFontSize = if (calendarState.viewType == CalendarViewType.MONTHLY) 22.sp else 18.sp
	val animatedFontSize by animateFloatAsState(
		targetValue = targetFontSize.value,
		animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
		label = "Header Font Size"
	)
	val targetStartPadding = if (calendarState.viewType == CalendarViewType.MONTHLY) 8.dp else 0.dp
	val animatedStartPadding by animateDpAsState(
		targetValue = targetStartPadding,
		animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
		label = "Header Padding"
	)
	
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(56.dp)
			.padding(horizontal = 16.dp)
	) {
		
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.align(BiasAlignment(horizontalBias = animatedBias, verticalBias = 0f))
				.padding(start = animatedStartPadding)
		) {
			val yearMonth = calendarState.displayedYearMonth
			val headerText = if (calendarState.viewType == CalendarViewType.MONTHLY) {
				"${yearMonth.year}. ${yearMonth.monthValue}"
			} else {
				"${yearMonth.monthValue}월"
			}
			Text(
				text = headerText,
				fontSize = animatedFontSize.sp,
				fontWeight = FontWeight.SemiBold
			)
		}
		
		Row(
			modifier = Modifier
				.align(Alignment.CenterEnd)
				.padding(end = 4.dp)
		) {
			IconButton(onClick = onToggleViewType) {
				Icon(
					imageVector = if (calendarState.viewType == CalendarViewType.WEEKLY)
						Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
					contentDescription = "달력 펼치기/접기",
					modifier = Modifier.size(22.dp)
				)
			}
			IconButton(onClick = onMonthPickerClick) {
				Icon(
					imageVector = Icons.Default.CalendarMonth,
					contentDescription = "월 선택",
					modifier = Modifier.size(26.dp)
				)
			}
			IconButton(onClick = onGoToToday) {
				Icon(
					imageVector = Icons.Default.AddLocation,
					contentDescription = "오늘로 이동",
					modifier = Modifier.size(26.dp)
				)
			}
		}
	}
}

@Composable
private fun WeekDays() {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp)
	) {
		listOf("일", "월", "화", "수", "목", "금", "토").forEach { dayText ->
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier.weight(1f)
			) {
				Text(
					text = dayText,
					fontSize = 12.sp,
					fontWeight = FontWeight.Medium,
					color = if (dayText == "일") Color.Red else Color.Black
				)
			}
		}
	}
	Spacer(modifier = Modifier.height(8.dp))
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarGrid(
	calendarState: CalendarState,
	pagerState: PagerState,
	onDateClick: (LocalDate) -> Unit
) {
	BoxWithConstraints(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp)
	) {
		val dayHeight = maxWidth / 7
		val monthlyWeekCount = if (calendarState.viewType == CalendarViewType.MONTHLY) {
			calendarState.calendarDates.size / 7
		} else 1
		
		val targetHeight = if (calendarState.viewType == CalendarViewType.MONTHLY) {
			dayHeight * monthlyWeekCount
		} else {
			dayHeight
		}
		
		val animatedHeight by animateDpAsState(
			targetValue = targetHeight,
			animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
			label = "CalendarGridHeightAnimation"
		)
		
		Box(modifier = Modifier.height(animatedHeight)) {
			HorizontalPager(state = pagerState) {
				Column {
					calendarState.calendarDates.chunked(7).forEach { week ->
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.height(dayHeight)
						) {
							week.forEach { date ->
								CalendarDayBox(
									date = date,
									calendarState = calendarState,
									onClick = { onDateClick(date) }
								)
							}
						}
					}
				}
			}
		}
	}
}

@Composable
private fun CalendarFooter(selectedDateInfo: String) {
	Spacer(modifier = Modifier.height(8.dp))
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.End
	) {
		Text(
			text = selectedDateInfo,
			style = MaterialTheme.typography.titleSmall,
			color = Color.Gray,
			modifier = Modifier.padding(end = 16.dp)
		)
	}
	Spacer(modifier = Modifier.height(8.dp))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RowScope.CalendarDayBox(
	date: LocalDate,
	calendarState: CalendarState,
	onClick: () -> Unit
) {
	Box(
		modifier = Modifier
			.weight(1f)
			.fillMaxHeight()
	) {
		val isSelected = date == calendarState.selectedDate
		val isVisibleMonth = YearMonth.from(date) == calendarState.displayedYearMonth
		val isToday = date == LocalDate.now()
		
		val hasLoggedMeal = calendarState.markedDates.contains(date)
		
		CalendarDay(
			date = date,
			isToday = isToday,
			isSelected = isSelected,
			isVisibleMonth = if (calendarState.viewType == CalendarViewType.WEEKLY) true else isVisibleMonth,
			hasLoggedMeal = hasLoggedMeal,
			onClick = onClick
		)
	}
}