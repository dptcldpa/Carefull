package com.cases.carefull.features.carefullcontents.diagnosis.chatbot

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val diseaseName: String? = null,
    val recommendedDepartment: String? = null
)