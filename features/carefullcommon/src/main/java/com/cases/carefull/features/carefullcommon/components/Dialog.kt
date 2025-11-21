package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun CommonAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
    textContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    titleTextStyle: TextStyle = MaterialTheme.typography.headlineSmall,
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = properties,
        title = {
            ProvideTextStyle(value = titleTextStyle) {
                title?.invoke()
            }
        },
        text = content,
        shape = RoundedCornerShape(12.dp),
        containerColor = containerColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}

@Preview(showBackground = true)
@Composable
fun CommonAlertDialogPreview() {
    CommonAlertDialog(
        onDismissRequest = {},
        title = { Text("알림") },
        content = { Text("이 작업을 정말로 실행하시겠습니까?") },
        confirmButton = {
            TextButton(onClick = {}) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = {}) {
                Text("취소")
            }
        }
    )
}

