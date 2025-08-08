package com.cases.carefull.features.carefullcommon.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cases.carefull.features.carefullcommon.R
import java.time.LocalDate
import java.time.YearMonth


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
	modifier: Modifier,
	state: CalendarState,
	onClick: () -> Unit
) {
	LaunchedEffect(state.datePagerState.currentPage) {
		// datePager 현재 페이지가 변경되면(직접 넘겨도) 선택된 날짜를 현재 페이지 첫째 날로 변경
		state.currentPageYM.takeIf { it != YearMonth.from(state.selectedDate) }
			?.let { state.selectedDate = it.atDay(1) }
	}
	
	LaunchedEffect(state.selectedDate) {
		// 선택된 날짜가 현재 페이지에 없으면, 해당 페이지로 datePager 스크롤
		state.currentPageYM.takeIf { it != YearMonth.from(state.selectedDate) }?.let { pageMonth ->
			state.datePagerState
				.animateScrollToPage(state.datePagerState.currentPage + (1.takeIf {
					state.selectedDate.isAfter(
						pageMonth.atEndOfMonth()
					)
				} ?: -1))
		}
	}
	
	Column(
		modifier = modifier
			.fillMaxSize()
			.background(Color.White)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp)
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.align(Alignment.Center)
					.padding(start = 8.dp)
					.clickable(
						interactionSource = remember { MutableInteractionSource() },
						indication = null,
						onClick = { }
					)
			) {
				Text(
					text = buildAnnotatedString {
						withStyle(
							block = {
								append(state.currentPageYM.run {
									takeIf { year != LocalDate.now().year }?.let { "${year}. $monthValue" }
										?: "$monthValue"
								})
							},
							style = SpanStyle(
								fontSize = 23.sp,
								fontWeight = FontWeight.Medium,
								baselineShift = BaselineShift(-0.015f)
							)
						)
						state.currentPageYM.takeIf { it.year == LocalDate.now().year }
							?.let { append("월") }
					},
					fontSize = 22.sp,
					lineHeight = 22.sp,
					fontWeight = FontWeight.SemiBold
				)
				IconButton(
					onClick = {},
					content = {
						Icon(
							imageVector = Icons.Default.ArrowDropDown,
							contentDescription = "날짜 선택",
							modifier = Modifier
								.size(22.dp)
						)
						
					}
				)
			}
			Row(
				modifier = Modifier
					.align(Alignment.CenterEnd)
					.padding(end = 4.dp)
			) {
				IconButton(
					onClick = { },
					content = {
						Icon(
							painter = painterResource(R.drawable.app_logo),
							contentDescription = "추가",
							modifier = Modifier
								.size(26.dp)
						)
					}
				)
				IconButton(
					onClick = { },
					content = {
						Icon(
							painter = painterResource(R.drawable.app_logo),
							contentDescription = "알림",
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
		HorizontalPager(
			state = state.datePagerState,
			modifier = Modifier
				.weight(1f)
				.padding(horizontal = 8.dp)
		) { page ->
			val pageYearMonth = remember {
				YearMonth.from(state.currentDate)
					.plusMonths((page - Int.MAX_VALUE / 2).toLong())
			}
			val dayOfMonth = remember { state.getDaysOfMonth(pageYearMonth) }
			Column(
				modifier = Modifier
					.fillMaxSize()
			) {
				dayOfMonth.chunked(7).forEach { week ->
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
					) {
						week.forEach { date ->
							Box(
								modifier = Modifier
									.weight(1f)
									.fillMaxHeight()
							) {
								val isToday = remember(
									date,
									state.currentDate
								) { date == state.currentDate }
								val isSelected = remember(
									date,
									state.selectedDate
								) { date == state.selectedDate }
								val isVisibleMonth =
									remember { YearMonth.from(date) == pageYearMonth }
								
								CalendarDay(
									date = date,
									isToday = isToday,
									isSelected = isSelected,
									isVisibleMonth = isVisibleMonth,
									onClick = {
										state.selectedDate = date
										onClick()
									}
								)
							}
						}
					}
				}
			}
		}
	}
}