package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.ChatbotMessage


interface ChatbotRepository {
    suspend fun sendMessage(prompt: String): ChatbotMessage
}