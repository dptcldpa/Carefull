package com.cases.carefull.features.carefullmainui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cases.carefull.domain.model.CalendarViewType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs

@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
	modifier: Modifier,
	state: CalendarState,
	onClick: () -> Unit,
	onMonthPickerClick: () -> Unit
) {
	LaunchedEffect(state.pagerState, state.viewType) {
		snapshotFlow { state.pagerState.currentPage }.collect { page ->
			
			if (state.isProgrammaticScroll) return@collect
			
			val newDate = if (state.viewType == CalendarViewType.MONTHLY) {
				// 월간 뷰: 현재 페이지의 1일을 대표 날짜로 설정
				state.currentDate.plusMonths((page - CalendarState.START_PAGE_MONTH).toLong())
					.withDayOfMonth(1)
			} else {
				// 주간 뷰: 현재 페이지의 시작일(일요일)을 대표 날짜로 설정
				state.currentDate.plusWeeks((page - CalendarState.START_PAGE_WEEK).toLong())
					.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
			}
			
			// selectedDate가 현재 보이는 범위에 없다면, 대표 날짜로 업데이트
			val currentYearMonth = YearMonth.from(state.selectedDate)
			val newYearMonth = YearMonth.from(newDate)
			if (currentYearMonth != newYearMonth && state.viewType == CalendarViewType.MONTHLY) {
				state.selectedDate = newDate
			} else if (state.viewType == CalendarViewType.WEEKLY) {
				val startOfWeek = newDate
				val endOfWeek = startOfWeek.plusDays(6)
				if (state.selectedDate.isBefore(startOfWeek) || state.selectedDate.isAfter(endOfWeek)) {
					state.selectedDate = startOfWeek
				}
			}
		}
	}
	
	val targetBias = if (state.viewType == CalendarViewType.MONTHLY) 0f else -1f
	val animatedBias by animateFloatAsState(
		targetValue = targetBias,
		animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
		label = "Header Alignment Bias"
	)
	val targetFontSize = if (state.viewType == CalendarViewType.MONTHLY) 26.sp else 18.sp
	val animatedFontSize by animateFloatAsState(
		targetValue = targetFontSize.value,
		animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
		label = "Header Font Size"
	)
	val targetStartPadding = if (state.viewType == CalendarViewType.MONTHLY) 8.dp else 0.dp
	val animatedStartPadding by animateDpAsState(
		targetValue = targetStartPadding,
		animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
		label = "Header Padding"
	)
	var totalDragY by remember { mutableFloatStateOf(0f) }
	val swipeThreshold = with(LocalDensity.current) { 50.dp.toPx() }
	
	Column(
		modifier = modifier
			.fillMaxWidth()
			.background(Color.White)
			.animateContentSize()
			.pointerInput(Unit) {
				detectVerticalDragGestures(
					onDragStart = {
						totalDragY = 0f
					},
					onVerticalDrag = { _, dragAmount ->
						totalDragY += dragAmount
					},
					onDragEnd = {
						if (abs(totalDragY) > swipeThreshold) {
							if (totalDragY > 0) {
								// 아래로 스와이프: 월간 뷰로 전환
								state.updateViewType(CalendarViewType.MONTHLY)
							} else {
								// 위로 스와이프: 주간 뷰로 전환
								state.updateViewType(CalendarViewType.WEEKLY)
							}
						}
					}
				)
			}
	) {
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
				Text(
					text = buildAnnotatedString {
						withStyle(
							block = {
								val yearMonth = state.currentPageYearMonth
								if (state.viewType == CalendarViewType.MONTHLY) {
									append("${yearMonth.year}. ${yearMonth.monthValue}")
								} else {
									append("${yearMonth.monthValue}월")
								}
							},
							style = SpanStyle(
								fontSize = animatedFontSize.sp,
								fontWeight = FontWeight.Medium,
								baselineShift = BaselineShift(-0.015f)
							)
						)
					},
					fontSize = animatedFontSize.sp,
					lineHeight = 22.sp,
					fontWeight = FontWeight.SemiBold
				)
			}
			
			Row(
				modifier = Modifier
					.align(Alignment.CenterEnd)
					.padding(end = 4.dp)
			) {
				IconButton(
					onClick = {
						val newType = if (state.viewType == CalendarViewType.WEEKLY)
							CalendarViewType.MONTHLY else CalendarViewType.WEEKLY
						state.updateViewType(newType)
					},
					content = {
						Icon(
							imageVector = if (state.viewType == CalendarViewType.WEEKLY)
								Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
							contentDescription = "주간/월간 달력 변경",
							modifier = Modifier
								.size(22.dp)
						)
					}
				)
				IconButton(
					onClick = { onMonthPickerClick() },
					content = {
						Icon(
							imageVector = Icons.Default.CalendarMonth,
							contentDescription = "직접 이동",
							modifier = Modifier
								.size(26.dp)
						)
					}
				)
				IconButton(
					onClick = { state.goToToday() },
					content = {
						Icon(
							imageVector = Icons.Default.AddLocation,
							contentDescription = "오늘로 이동",
							modifier = Modifier
								.size(26.dp)
						)
					}
				)
			}
		}
		
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp)
		) {
			listOf("일", "월", "화", "수", "목", "금", "토").forEach { dayText ->
				Box(
					contentAlignment = Alignment.Center,
					modifier = Modifier
						.weight(1f)
						.padding(horizontal = 8.dp)
				) {
					Text(
						text = dayText,
						fontSize = 12.sp,
						lineHeight = 17.sp,
						fontWeight = FontWeight.Medium,
						color = Color.Red.takeIf { dayText == "일" } ?: Color.Black
					)
				}
			}
		}
		
		
		BoxWithConstraints(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp)
		) {
			val dayHeight = maxWidth / 7
			
			Spacer(modifier = Modifier.height(16.dp))
			
			HorizontalPager(
				state = state.pagerState,
				modifier = Modifier
			) { page ->
				when (state.viewType) {
					CalendarViewType.MONTHLY -> {
						val pageYearMonth = remember {
							state.currentDate.plusMonths((page - CalendarState.START_PAGE_MONTH).toLong())
								.let { YearMonth.from(it) }
						}
						val daysOfMonth =
							remember(pageYearMonth) { state.getDaysOfMonth(pageYearMonth) }
						
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.height(dayHeight * 6)
						) {
							daysOfMonth.chunked(7).forEach { week ->
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.height(dayHeight)
								) {
									week.forEach { date ->
										CalendarDayBox(date, state, onClick)
									}
								}
							}
						}
					}
					
					CalendarViewType.WEEKLY -> {
						val pageDate = remember {
							state.currentDate.plusWeeks((page - CalendarState.START_PAGE_WEEK).toLong())
						}
						val daysOfWeek =
							remember(pageDate) { state.getDaysOfWeek(pageDate) }
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.height(dayHeight)
						) {
							daysOfWeek.forEach { date ->
								CalendarDayBox(date, state, onClick)
							}
						}
					}
				}
			}
		}
	}
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RowScope.CalendarDayBox(
	date: LocalDate,
	state: CalendarState,
	onClick: () -> Unit
) {
	Box(
		modifier = Modifier
			.weight(1f)
			.fillMaxHeight()
	) {
		val isSelected = remember(date, state.selectedDate) { date == state.selectedDate }
		val isVisibleMonth = remember(
			date,
			state.currentPageYearMonth
		) { YearMonth.from(date) == state.currentPageYearMonth }
		
		CalendarDay(
			date = date,
			isToday = date == state.currentDate,
			isSelected = isSelected,
			isVisibleMonth = if (state.viewType == CalendarViewType.WEEKLY) true else isVisibleMonth,
			onClick = {
				state.selectedDate = date
				onClick()
			}
		)
	}
}
