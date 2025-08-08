package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    MenuContainer(
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SwitchMenuButton(text: String) {
    var isChecked by remember { mutableStateOf(false) }
    MenuContainer(modifier = Modifier)
    {
        Text(
            text = text,
            modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
        )
    }
}

@Composable
fun RowLine() {
    Box(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth(0.8f)
            .background(color = Color.Gray)
    )
}

@Composable
private fun MenuContainer(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .defaultMinSize(minHeight = 45.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}