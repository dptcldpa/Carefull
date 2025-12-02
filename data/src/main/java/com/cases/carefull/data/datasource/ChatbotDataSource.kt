package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.diagnosis.StructuredContentDto

interface ChatbotDataSource {
    suspend fun sendMessage(prompt: String): StructuredContentDto
}