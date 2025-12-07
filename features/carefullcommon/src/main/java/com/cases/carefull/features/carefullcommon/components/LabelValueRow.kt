package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme

@Composable
fun LabelValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String = "",
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (isHighlighted) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = unit,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LabelValueRowPreview() {
    CarefullTheme {
        LabelValueRow(
            label = "기초 대사량",
            unit = "kcal",
            value = "1767",
            isHighlighted = true
        )
    }
}
