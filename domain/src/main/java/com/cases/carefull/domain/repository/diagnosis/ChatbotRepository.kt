package com.cases.carefull.domain.repository.diagnosis

import com.cases.carefull.domain.model.ChatbotMessage


interface ChatbotRepository {
    suspend fun sendMessage(prompt: String): ChatbotMessage
}