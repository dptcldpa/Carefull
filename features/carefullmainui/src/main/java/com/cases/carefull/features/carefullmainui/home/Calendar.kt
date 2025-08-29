package com.cases.carefull.features.carefullmainui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cases.carefull.domain.model.CalendarViewType
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
	modifier: Modifier,
	uiState: HomeUiState,
	pagerState: PagerState,
	onDateClick: (LocalDate) -> Unit,
	onToggleViewType: () -> Unit,
	onMonthPickerClick: () -> Unit,
	onGoToToday: () -> Unit
) {
	
	val targetBias = if (uiState.viewType == CalendarViewType.MONTHLY) 0f else -1f
	val animatedBias by animateFloatAsState(
		targetValue = targetBias,
		animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
		label = "Header Alignment Bias"
	)
	val targetFontSize = if (uiState.viewType == CalendarViewType.MONTHLY) 22.sp else 18.sp
	val animatedFontSize by animateFloatAsState(
		targetValue = targetFontSize.value,
		animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
		label = "Header Font Size"
	)
	val targetStartPadding = if (uiState.viewType == CalendarViewType.MONTHLY) 8.dp else 0.dp
	val animatedStartPadding by animateDpAsState(
		targetValue = targetStartPadding,
		animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
		label = "Header Padding"
	)
	var totalDragY by remember { mutableFloatStateOf(0f) }
	val swipeThreshold = with(LocalDensity.current) { 50.dp.toPx() }
	
	Surface(
		modifier = modifier
			.fillMaxWidth()
			.animateContentSize()
			.pointerInput(Unit) {
				detectVerticalDragGestures(
					onDragStart = { totalDragY = 0f },
					onVerticalDrag = { _, dragAmount -> totalDragY += dragAmount },
					onDragEnd = {
						if (abs(totalDragY) > swipeThreshold) {
							onToggleViewType()
						}
					}
				)
			},
		shape = RoundedCornerShape(16.dp),
		border = BorderStroke(1.dp, Color.LightGray),
		color = Color.White
	) {
		Column {
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
									val yearMonth = uiState.displayedYearMonth
									if (uiState.viewType == CalendarViewType.MONTHLY) {
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
					IconButton(onClick = onToggleViewType) {
						Icon(
							imageVector = if (uiState.viewType == CalendarViewType.WEEKLY)
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
							lineHeight = 17.sp,
							fontWeight = FontWeight.Medium,
							color = if (dayText == "일") Color.Red else Color.Black
						)
					}
				}
			}
			Spacer(modifier = Modifier.height(8.dp))
			
			BoxWithConstraints(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 8.dp)
			) {
				val dayHeight = maxWidth / 7
				
				HorizontalPager(state = pagerState) {
					Column {
						uiState.calendarDates.chunked(7).forEach { week ->
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.height(dayHeight)
							) {
								week.forEach { date ->
									CalendarDayBox(
										date = date,
										uiState = uiState,
										onClick = { onDateClick(date) }
									)
								}
							}
						}
					}
				}
			}
			Spacer(modifier = Modifier.height(8.dp))
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RowScope.CalendarDayBox(
	date: LocalDate,
	uiState: HomeUiState,
	onClick: () -> Unit
) {
	Box(
		modifier = Modifier
			.weight(1f)
			.fillMaxHeight()
	) {
		val isSelected = date == uiState.selectedDate
		val isVisibleMonth = YearMonth.from(date) == uiState.displayedYearMonth
		val isToday = date == LocalDate.now()
		
		CalendarDay(
			date = date,
			isToday = isToday,
			isSelected = isSelected,
			isVisibleMonth = if (uiState.viewType == CalendarViewType.WEEKLY) true else isVisibleMonth,
			onClick = onClick
		)
	}
}
