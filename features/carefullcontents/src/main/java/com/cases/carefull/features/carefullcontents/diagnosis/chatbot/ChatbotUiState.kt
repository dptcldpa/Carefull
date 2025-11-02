package com.cases.carefull.features.carefullcontents.diagnosis.chatbot

data class ChatbotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)