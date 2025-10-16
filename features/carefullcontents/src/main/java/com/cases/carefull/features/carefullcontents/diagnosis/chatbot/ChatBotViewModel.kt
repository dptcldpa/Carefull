package com.cases.carefull.chatbot

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.cases.carefull.domain.model.ChatBotInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatBotViewModel @Inject constructor(
) : ViewModel() {
    val chatMessages = mutableStateListOf<ChatBotInfo>()

    init {
        chatMessages.add(ChatBotInfo("안녕하세요! 챗봇입니다.", false))
    }

    fun onUserMessage(input: String) {
        if (input.isBlank()) return

        chatMessages.add(ChatBotInfo(input, true))

        val (diagnosis, department) = generateResponse(input)


        if (diagnosis.isNotEmpty()) {
            val fullMessage = diagnosis

            chatMessages.add(
                ChatBotInfo(
                    message = fullMessage,
                    isUser = false,
                    clickableDepartments = department,
                    onClickDepartment = {
                    }
                )
            )
        } else {
            chatMessages.add(
                ChatBotInfo(
                    message = "증상에 해당하는 질병을 찾을 수 없습니다.",
                    isUser = false
                )
            )
        }
    }

    private fun generateResponse(input: String): Pair<String, List<String>> {
        val matchedDiseases = mutableListOf<String>()
        val departments = mutableSetOf<String>()

        if (input.contains("cough")) {
            matchedDiseases.add("cold")
            departments.add("내과")
        }

        if (input.contains("기침")) {
            matchedDiseases.add("감기")
            departments.add("내과")
        }
        if (input.contains("열")) {
            matchedDiseases.add("독감")
            departments.add("내과")
        }
        if (input.contains("두통")) {
            matchedDiseases.add("코로나19")
            departments.add("내과")
            departments.add("신경과")
        }

        return if (matchedDiseases.isEmpty()) {
            "" to emptyList()
        } else {
            "열, 목통증, 기침… 전형적으로 호흡기 감염 쪽이 의심돼요. 흔히 감기(상기도 감염)일 수도 있지만, 요즘 같은 시기엔 독감이나 코로나 같은 바이러스성 질환도 염두에 두는 게 좋아요. 고열이 오래가거나, 숨이 차거나, 가래에 피가 섞이면 빨리 병원 가야 하고요. 증상이 심하지 않아도 진료 받아두는 게 안전합니다.\n" to departments.toList()
        }
    }
}