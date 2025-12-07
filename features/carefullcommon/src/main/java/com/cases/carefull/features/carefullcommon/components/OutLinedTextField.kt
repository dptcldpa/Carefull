package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme
import kotlinx.coroutines.flow.collectLatest


@Composable
fun NumberOutLinedTextField(
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
fun TextOutLinedTextField(
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

@Composable
fun UnitTextField(
    modifier: Modifier= Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String = ""
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = value)) }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    if (textFieldValue.text != value) {
        textFieldValue = textFieldValue.copy(text = value)
    }

    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            textFieldValue = textFieldValue.copy(
                text = value,
                selection = TextRange(value.length)
            )
        }
    }

    LaunchedEffect(isFocused, interactionSource) {
        interactionSource.interactions.collectLatest { interaction ->
            if (isFocused && interaction is PressInteraction.Release) {
                textFieldValue = textFieldValue.copy(
                    selection = TextRange(0, textFieldValue.text.length)
                )
            }
        }
    }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            textFieldValue = textFieldValue.copy(
                selection = TextRange(0, textFieldValue.text.length)
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onValueChange(newValue.text)
            },
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 5.dp),
            interactionSource = interactionSource,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.End,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(30.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NumberOutLinedTextFieldPreview() {
    CarefullTheme {
        NumberOutLinedTextField(
            modifier = Modifier,
            value = "130",
            onValueChange = {},
            label = { Text("탄수화물") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TextOutLinedTextFieldPreview() {
    CarefullTheme {
        TextOutLinedTextField(
            modifier = Modifier,
            value = "김밥",
            onValueChange = {},
            label = { Text("음식명") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UnitTextFieldPreview() {
    CarefullTheme {
        UnitTextField(
            modifier = Modifier,
            label = "신장",
            value = "180",
            onValueChange = {},
            unit = "cm"
        )
    }
}
