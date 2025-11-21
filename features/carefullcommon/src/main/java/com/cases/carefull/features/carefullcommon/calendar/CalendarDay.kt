package com.cases.carefull.features.carefullcommon.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun CalendarDay(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    isVisibleMonth: Boolean,
    hasLoggedMeal: Boolean,
    hasCompletedDailyExercise: Boolean,
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
            Row {
                if (hasCompletedDailyExercise) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.completion_workout_today),
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                if (hasLoggedMeal) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.completion_diet_record),
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CalendarDayPreview_AllStates() {
    CarefullTheme {
        Column(modifier = Modifier.size(50.dp)) {
            CalendarDay(
                date = LocalDate.now(),
                isToday = true,
                isSelected = true,
                isVisibleMonth = true,
                hasLoggedMeal = true,
                hasCompletedDailyExercise = true,
                onClick = {}
            )
        }
    }
}
