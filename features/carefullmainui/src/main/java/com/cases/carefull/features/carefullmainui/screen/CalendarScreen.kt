package com.cases.carefull.features.carefullmainui.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cases.carefull.features.carefullcommon.components.Calendar
import com.cases.carefull.features.carefullcommon.components.CalendarSize
import com.cases.carefull.features.carefullcommon.components.rememberCalendarState
import java.time.format.DateTimeFormatter


@SuppressLint("UnusedBoxWithConstraintsScope", "ConfigurationScreenWidthHeight")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
	val configuration = LocalConfiguration.current
	
	BoxWithConstraints(
		modifier = Modifier
			.statusBarsPadding()
	) {
		val halfHeight = remember { maxHeight / 2 }
		val fullHeight = remember { maxHeight }
		val state = rememberCalendarState()
		var calendarHeight by remember { mutableStateOf(if (state.snapState == CalendarSize.FULL) fullHeight else halfHeight) }
		// 부드러운 애니메이션
		val animatedHeight by animateDpAsState(calendarHeight)
		
		Column(
			modifier = Modifier
				.pointerInput(Unit) {
					detectVerticalDragGestures(
						onVerticalDrag = { change, dragAmount ->
							change.consume()
							// 상하 무한 제스처 방지
							calendarHeight = (calendarHeight + dragAmount.toDp()).coerceIn(
								halfHeight,
								fullHeight
							)
						},
						onDragEnd = {
							when (state.snapState) {
								CalendarSize.HALF -> if (calendarHeight > halfHeight) {
									state.snapState = CalendarSize.FULL
									calendarHeight = fullHeight
								}
								
								CalendarSize.FULL -> if (calendarHeight < fullHeight) {
									state.snapState = CalendarSize.HALF
									calendarHeight = halfHeight
								}
							}
						}
					)
				}
		) {
			Calendar(
				state = state,
				onClick = {
					state.snapState = CalendarSize.HALF
					calendarHeight = halfHeight
				},
				modifier = Modifier
					.fillMaxWidth()
					.height(animatedHeight)
			)
			HorizontalDivider(
				color = Color(0xFFE0E0E0),
				thickness = 1.dp,
				modifier = Modifier
					.padding(16.dp, 8.dp, 16.dp, 0.dp)
			)
			Column(
				modifier = Modifier
					.fillMaxWidth()
					// BoxWithConstraints는 height에서 사용할 수 없음
					.height(configuration.screenHeightDp.dp - animatedHeight)
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(4.dp),
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.fillMaxWidth()
						.height(48.dp)
						.padding(horizontal = 20.dp)
				) {
					Text(
						text = state.calculateDaysDifference(),
						fontSize = 17.sp,
						lineHeight = 17.sp,
						fontWeight = FontWeight.SemiBold
					)
					Box(
						modifier = Modifier
							.size(2.dp)
							.background(Color.Black, CircleShape)
					)
					Text(
						text = state.selectedDate.format(DateTimeFormatter.ofPattern("M. d. (E)")),
						fontSize = 15.sp,
						lineHeight = 15.sp,
						fontWeight = FontWeight.Normal,
						color = Color(0xFF999999)
					)
				}
			}
		}
	}
}