package com.cases.carefull.domain.model

data class ChatbotMessage(
    val content: String,
    val suggestedActions: List<SuggestedAction>
)
sealed class SuggestedAction(
    open val button: String
) {
    data class FindHospital(
        override val button: String,
        val department: String
    ) : SuggestedAction(button)

    data class ShowDiseaseInfo(
        override val button: String,
        val diseaseName: String
    ) : SuggestedAction(button)
}