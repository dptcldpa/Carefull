package com.cases.carefull.features.carefullcontents.diagnosis.chatbot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cases.carefull.chatbot.ChatBotViewModel
import com.cases.carefull.domain.model.ChatBotInfo

@Composable
fun ChatBotScreen(
    modifier: Modifier = Modifier,
    onDepartmentClick: (department: String, diagnosis: String) -> Unit
) {
    val viewModel = remember { ChatBotViewModel(onDepartmentClick) }

    var userInput by remember { mutableStateOf("") }
    val messages = viewModel.chatMessages.reversed()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatBubble(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = userInput,
                onValueChange = { userInput = it },
                placeholder = { Text("증상을 입력하세요") },
                shape = RoundedCornerShape(16.dp)
            )
            IconButton(onClick = {
                viewModel.onUserMessage(userInput)
                userInput = ""
            }) {
               Icon(
                   imageVector = Icons.AutoMirrored.Filled.Send,
                   tint = MaterialTheme.colorScheme.primary,
                   contentDescription = "전송"
               )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatBotInfo) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = bgColor,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = message.message,
                    color = textColor
                )
                if (!message.clickableDepartments.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "추천 진료과:",
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        message.clickableDepartments?.forEach { dept ->
                            AssistChip(
                                onClick = { message.onClickDepartment?.invoke(dept) },
                                label = { Text(dept) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
