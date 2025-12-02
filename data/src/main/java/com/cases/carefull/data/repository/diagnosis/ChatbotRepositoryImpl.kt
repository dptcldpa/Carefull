package com.cases.carefull.data.repository.diagnosis

import com.cases.carefull.data.datasource.ChatbotDataSource
import com.cases.carefull.data.dto.diagnosis.StructuredContentDto
import com.cases.carefull.domain.model.ChatbotMessage
import com.cases.carefull.domain.model.SuggestedAction
import com.cases.carefull.domain.repository.diagnosis.ChatbotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatbotRepositoryImpl @Inject constructor(
    private val chatbotDataSource: ChatbotDataSource
) : ChatbotRepository {

    override suspend fun sendMessage(prompt: String): ChatbotMessage = withContext(Dispatchers.IO){
        val responseDto = chatbotDataSource.sendMessage(prompt)

         responseDto.toDomainModel()
    }
}

private fun StructuredContentDto.toDomainModel(): ChatbotMessage {
    val domainActions = this.suggestedActions.mapNotNull { dto ->
        when {
            dto.department != null -> SuggestedAction.FindHospital(
                button = dto.button,
                department = dto.department
            )
            dto.diseaseName != null -> SuggestedAction.ShowDiseaseInfo(
                button = dto.button,
                diseaseName = dto.diseaseName
            )
            else -> null
        }
    }
    return ChatbotMessage(
        content = this.content,
        suggestedActions = domainActions
    )
}