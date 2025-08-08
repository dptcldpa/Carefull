package com.cases.carefull.features.carefullcommon.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDay(
	date: LocalDate,
	isToday: Boolean,
	isSelected: Boolean,
	isVisibleMonth: Boolean,
	onClick: () -> Unit
) {
	// 날짜 텍스트 배경색 - 일요일은 빨강, 나머지는 검정
	val circleColor = remember(isToday, date.dayOfWeek) {
		when {
			isToday && date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
			isToday -> Color.Black
			else -> Color.Transparent
		}
	}
	// 날짜 글자색 - 오늘이라면 배경색과 대비되는 하양,
	// 그외 일요일은 빨강, 나머지는 검정으로 하되 현재 달이 아니라면 투명도 조절
	val textColor = remember(isToday, isVisibleMonth) {
		when {
			isToday -> Color.White
			!isVisibleMonth && date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red.copy(alpha = 0.3f)
			!isVisibleMonth -> Color.DarkGray.copy(alpha = 0.3f)
			date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
			else -> Color.Black
		}
	}
	
	Surface(
		modifier = Modifier.clickable(
			interactionSource = remember { MutableInteractionSource() },
			indication = null,
			onClick = onClick),
		color = Color.White,
		shape = RoundedCornerShape(6.dp),
		border = BorderStroke(1.dp, Color.Black).takeIf { isSelected }
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxSize()
		) {
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier
					.padding(top = 2.5.dp)
					.size(20.dp)
					.background(circleColor, CircleShape)
			) {
				Text(
					text = date.dayOfMonth.toString(),
					fontSize = 12.sp,
					lineHeight = 12.sp,
					fontWeight = FontWeight.Normal,
					color = textColor
				)
			}
		}
	}
}
