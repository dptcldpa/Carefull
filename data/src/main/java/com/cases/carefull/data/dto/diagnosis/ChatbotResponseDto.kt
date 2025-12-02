package com.cases.carefull.data.dto.diagnosis

import com.google.gson.annotations.SerializedName

data class ChatbotResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("output") val output: List<OutputMessageDto>
)

data class OutputMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: List<ContentDto>
)

data class ContentDto(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String
)

data class StructuredContentDto(
    @SerializedName("content") val content: String,
    @SerializedName("suggestedActions") val suggestedActions: List<SuggestedActionDto>
)

data class SuggestedActionDto(
    @SerializedName("button") val button: String,
    @SerializedName("department") val department: String? = null,
    @SerializedName("diseaseName") val diseaseName: String? = null
)