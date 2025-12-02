package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.diagnosis.ChatbotRequestDto
import com.cases.carefull.data.dto.diagnosis.StructuredContentDto
import com.cases.carefull.data.network.ChatbotApiService
import com.google.gson.Gson
import javax.inject.Inject

class ChatbotDataSourceImpl @Inject constructor(
    private val chatbotApiService: ChatbotApiService,
) : ChatbotDataSource {
    private val gson = Gson()


    override suspend fun sendMessage(prompt: String): StructuredContentDto {
        val systemMessage = """
            You are a helpful medical assistant chatbot.
            The user will describe their symptoms in Korean.
            Your response MUST be a single JSON object and nothing else. Do not include any text before or after the JSON object.
            The JSON object must have two fields: "content" (a string for the main message) and "suggestedActions" (an array of objects).
            Each object in "suggestedActions" must have a "button" (a string for the button text) and exactly one of the following two fields: "department" (a string for a medical department) or "diseaseName" (a string for a symptom or disease).
            If there are no relevant suggestions, "suggestedActions" MUST be an empty array [].
            
            Example for a user input of "기침이 나고 열이 나요":
            {
              "content": "감기 또는 독감이 의심됩니다. 충분한 휴식을 취하고 수분을 섭취하세요.",
              "suggestedActions": [
                { "button": "이비인후과", "department": "이비인후과" },
                { "button": "내과", "department": "내과" },
                { "button": "감기", "diseaseName": "감기" }
              ]
            }
        """.trimIndent()

        val finalInput = systemMessage + prompt
        val request = ChatbotRequestDto(input = finalInput)

        val response = chatbotApiService.getChatCompletion(request)

        val contentJsonString = response.output?.firstOrNull()
            ?.content?.firstOrNull { it.type == "output_text" }
            ?.text
            ?: throw IllegalStateException("AI 응답에 'text' 필드가 없습니다.")

        return gson.fromJson(contentJsonString, StructuredContentDto::class.java)
    }
}