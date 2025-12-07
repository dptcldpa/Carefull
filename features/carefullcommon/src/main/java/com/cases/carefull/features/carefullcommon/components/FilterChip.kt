package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CommonFilterChipRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    itemKey: ((T) -> Any)? = null
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = items,
            key = itemKey
        ) { item ->
            FilterChip(
                selected = (item == selectedItem),
                onClick = { onItemSelected(item) },
                label = { Text(text = itemLabel(item)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommonFilterChipRowStringPreview() {
    var selectedItem by remember { mutableStateOf("전체") }
    val items = listOf("전체", "유산소", "무산소", "스트레칭", "요가", "필라테스")
    CarefullTheme {
        CommonFilterChipRow(
            items = items,
            selectedItem = selectedItem,
            onItemSelected = { selectedItem = it },
            itemLabel = { it },
            itemKey = { it }
        )
    }
}
