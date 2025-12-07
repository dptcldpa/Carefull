package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySummaryCard(
    title: String,
    currentValue: Int,
    goalText: String,
    onClick: () -> Unit,
    contentDescription: String,
    prefixText: String? = null,
    modifier: Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))

                val valueContent = @Composable {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            ) {
                                append("$currentValue")
                            }
                            append(goalText)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (prefixText != null) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = prefixText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        valueContent()
                    }
                } else {
                    valueContent()
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailySummaryCardPreview() {
    CarefullTheme {
        DailySummaryCard(
            title = "오늘의 운동",
            currentValue = 15,
            goalText = " / 30 회",
            onClick = {},
            contentDescription = "운동 기록 이동",
            modifier = Modifier,
            prefixText = "스쿼트"
        )
    }
}
