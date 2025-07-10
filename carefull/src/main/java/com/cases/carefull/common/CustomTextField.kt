package com.openstudy.carefull.common

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,                      // 현재 값
    onValueChange: (String) -> Unit,    // 값이 바뀔 때 호출될 함수
    labelResId: Int,                    // 라벨에 표시되는 StringResource값
    placeholderResId: Int,              // 플레이스홀더에 표시되는 StringResource값
    keyboardType: KeyboardType,         // 키보드 타입
    isPasswordTextField: Boolean = false// true시 입력값 '*' 출력 (비밀번호 입력에 사용)
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = {
            Text(text = stringResource(id = labelResId), style = MaterialTheme.typography.bodyLarge)
        },
        placeholder = {
            Text(
                text = stringResource(id = placeholderResId),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        visualTransformation = if (isPasswordTextField) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }
    )
}

