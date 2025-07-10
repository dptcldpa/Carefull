package com.openstudy.carefull.chatbot

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    val chatMessages = mutableStateListOf<ChatMessage>()

    init {
        chatMessages.add(ChatMessage("안녕하세요! 챗봇입니다.", false))
    }

    fun onUserMessage(input: String) {
        if (input.isBlank()) return

        chatMessages.add(ChatMessage(input, true))

        val response = generateResponse(input)
        chatMessages.add(ChatMessage(response, false))
    }

    private fun generateResponse(input: String): String {
        val matched = mutableListOf<String>()

        if (input.contains("기침")) matched.add("감기")
        if (input.contains("열")) matched.add("독감")
        if (input.contains("두통")) matched.add("코로나19")

        return if (matched.isEmpty()) "증상에 해당하는 질병을 찾을 수 없습니다."
        else "${matched.joinToString(", ")}가 의심됩니다."
    }
}