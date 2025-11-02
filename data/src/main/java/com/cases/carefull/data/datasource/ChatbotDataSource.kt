package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.StructuredContentDto

interface ChatbotDataSource {
    suspend fun sendMessage(prompt: String): StructuredContentDto
}