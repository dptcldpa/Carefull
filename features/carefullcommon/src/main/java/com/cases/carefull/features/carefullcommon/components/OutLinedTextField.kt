package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme


@Composable
fun CommonNumberOutLinedTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

@Composable
fun CommonTextOutLinedTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun CommonNumberOutLinedTextFieldPreview() {
    CarefullTheme {
        CommonNumberOutLinedTextField(
            modifier = Modifier,
            value = "130",
            onValueChange = {},
            label = { Text("탄수화물") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CommonTextOutLinedTextFieldPreview() {
    CarefullTheme {
        CommonTextOutLinedTextField(
            modifier = Modifier,
            value = "김밥",
            onValueChange = {},
            label = { Text("음식명") }
        )
    }
}
