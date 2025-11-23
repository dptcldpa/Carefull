package com.cases.carefull.features.carefullcontents.diagnosis.chatbot

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cases.carefull.chatbot.ChatBotViewModel
import com.cases.carefull.features.carefullcommon.components.SearchBar
import com.cases.carefull.features.carefullcommon.navigation.DiagnosisRoute

@Composable
fun ChatBotScreen(
    navController: NavController,
    viewModel: ChatBotViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var userInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is ChatNavigationEvent.ToHospitalList -> {
                    val destination = DiagnosisRoute.HospitalListScreen(
                        department = event.department,
                        diagnosis = ""
                    )
                    navController.navigate(destination)
                }

                is ChatNavigationEvent.ToDiseaseInfo -> {
                    Log.d("Navigation", "Navigate to Disease Info for: ${event.diseaseName}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages.reversed()) { message ->
                ChatBubble(
                    message = message,
                    onDepartmentClick = { department ->
                        viewModel.onHospitalButtonClicked(department)
                    },
                    onDiseaseClick = { disease ->
                        viewModel.onDiseaseInfoButtonClicked(disease)
                    }
                )
            }
        }
        SearchBar(
            modifier = Modifier,
            query = userInput,
            onQueryChange = { userInput = it },
            onSearch = {
                viewModel.sendMessage(userInput)
                userInput = ""
            },
            placeholder = "증상을 입력하세요",
            buttonIcon = Icons.AutoMirrored.Filled.Send
        )
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onDepartmentClick: (String) -> Unit,
    onDiseaseClick: (String) -> Unit
) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor =
        if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

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
                    text = message.content,
                    color = textColor
                )

                if (!message.isUser && (message.recommendedDepartment != null || message.diseaseName != null)) {

                    Spacer(modifier = Modifier.height(12.dp))

                    message.diseaseName?.let { disease ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "예상 병명 : ",
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            AssistChip(
                                onClick = { onDiseaseClick(disease) },
                                label = { Text(disease) }
                            )
                        }
                    }

                    message.recommendedDepartment?.let { dept ->
                        if (message.diseaseName != null) {
                            Spacer(modifier = Modifier.height(3.dp))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "관련 병원 : ",
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            AssistChip(
                                onClick = { onDepartmentClick(dept) },
                                label = { Text(dept) }
                            )
                        }
                    }
                }
            }
        }
    }
}
