package com.cases.carefull.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.SuggestedAction
import com.cases.carefull.domain.repository.diagnosis.ChatbotRepository
import com.cases.carefull.features.carefullcontents.diagnosis.chatbot.ChatMessage
import com.cases.carefull.features.carefullcontents.diagnosis.chatbot.ChatNavigationEvent
import com.cases.carefull.features.carefullcontents.diagnosis.chatbot.ChatbotUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val chatbotRepository: ChatbotRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatbotUiState())
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ChatNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        val welcomeMessage = ChatMessage("증상을 말씀해주세요.", isUser = false)
        _uiState.update { it.copy(messages = it.messages + welcomeMessage) }
    }

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = ChatMessage(content = prompt, isUser = true)
        _uiState.update { it.copy(messages = it.messages + userMessage, isLoading = true) }

        viewModelScope.launch {
            try {
                val responseDomainModel = chatbotRepository.sendMessage(prompt)

                val department = responseDomainModel.suggestedActions
                    .filterIsInstance<SuggestedAction.FindHospital>()
                    .firstOrNull()?.department

                val disease = responseDomainModel.suggestedActions
                    .filterIsInstance<SuggestedAction.ShowDiseaseInfo>()
                    .firstOrNull()?.diseaseName

                val botMessageUiModel = ChatMessage(
                    content = responseDomainModel.content,
                    isUser = false,
                    recommendedDepartment = department,
                    diseaseName = disease
                )

                _uiState.update { it.copy(messages = it.messages + botMessageUiModel, isLoading = false) }

            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "죄송합니다. AI 서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                    isUser = false
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + errorMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onHospitalButtonClicked(department: String) {
        viewModelScope.launch {
            _navigationEvent.emit(ChatNavigationEvent.ToHospitalList(department))
        }
    }

    fun onDiseaseInfoButtonClicked(diseaseName: String) {
        viewModelScope.launch {
            _navigationEvent.emit(ChatNavigationEvent.ToDiseaseInfo(diseaseName))
        }
    }
}