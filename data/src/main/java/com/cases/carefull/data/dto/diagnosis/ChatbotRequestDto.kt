package com.cases.carefull.data.dto.diagnosis

import com.google.gson.annotations.SerializedName

data class ChatbotRequestDto(
    @SerializedName("model") val model: String = "gpt-4.1",
    @SerializedName("input") val input: String
)