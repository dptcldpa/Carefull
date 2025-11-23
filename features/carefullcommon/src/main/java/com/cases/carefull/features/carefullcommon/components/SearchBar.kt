package com.cases.carefull.features.carefullcommon.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme

@Composable
fun SearchBar(
    modifier: Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    placeholder: String? = null,
    label: String? = null,
    buttonIcon: ImageVector = Icons.Default.Search
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = MaterialTheme.typography.bodyLarge,
                label = if (label != null) {
                    { Text(text = label) }
                } else null,
                placeholder = {
                    if (placeholder != null) {
                        Text(text = placeholder)
                    }
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "텍스트 삭제"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch()
                        keyboardController?.hide()
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            if (query.isNotBlank()) {
                IconButton(
                    onClick = {
                        onSearch()
                        keyboardController?.hide()
                        focusManager.clearFocus()

                    },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = buttonIcon,
                        contentDescription = "전송",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        Toast.makeText(context, "텍스트를 입력하세요", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = buttonIcon,
                        contentDescription = "텍스트 없음",
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SearchBarPreview_Empty() {
    var text by remember { mutableStateOf("") }

    CarefullTheme {
        SearchBar(
            modifier = Modifier,
            query = text,
            onQueryChange = { text = it },
            onSearch = { },
            placeholder = "음식을 검색하세요",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview_Filled() {
    var text by remember { mutableStateOf("닭가슴살") }

    CarefullTheme {
        SearchBar(
            modifier = Modifier,
            query = text,
            onQueryChange = { text = it },
            onSearch = { },
            placeholder = "음식을 검색하세요",
            label = "음식 검색",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview_Chatbot() {
    var text by remember { mutableStateOf("머리가 아파요") }
    CarefullTheme {
        SearchBar(
            modifier = Modifier,
            query = text,
            onQueryChange = { text = it },
            onSearch = { },
            placeholder = "증상을 물어보세요",
            buttonIcon = Icons.AutoMirrored.Filled.Send,
        )
    }
}
