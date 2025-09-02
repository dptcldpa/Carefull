package com.cases.carefull.features.carefullcommon.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
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
	hasLoggedMeal: Boolean,
	onClick: () -> Unit
) {
	val textColor = remember(isToday, isVisibleMonth) {
		when {
			!isVisibleMonth && date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red.copy(alpha = 0.3f)
			!isVisibleMonth -> Color.DarkGray.copy(alpha = 0.3f)
			date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
			else -> Color.Black
		}
	}
	val surfaceColor = remember(isToday) {
		if (isToday) {
			Color(0xFFE8F5E9)
		} else {
			Color.White
		}
	}
	
	Surface(
		modifier = Modifier.clickable(
			interactionSource = remember { MutableInteractionSource() },
			indication = null,
			onClick = onClick
		),
		color = surfaceColor,
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
			) {
				Text(
					text = date.dayOfMonth.toString(),
					fontSize = 12.sp,
					lineHeight = 12.sp,
					fontWeight = FontWeight.Normal,
					color = textColor
				)
			}
			if (hasLoggedMeal) {
				Icon(
					imageVector = Icons.Default.CheckCircle,
					contentDescription = "식단 기록 완료",
					modifier = Modifier.size(12.dp),
					tint = Color(0xFF4CAF50)
				)
			}
		}
	}
}
