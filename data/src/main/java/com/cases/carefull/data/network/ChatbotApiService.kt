package com.cases.carefull.data.network

import com.cases.carefull.data.dto.ChatbotRequestDto
import com.cases.carefull.data.dto.ChatbotResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApiService {
    @POST("v1/responses")
    suspend fun getChatCompletion(
        @Body request: ChatbotRequestDto
    ): ChatbotResponseDto
}