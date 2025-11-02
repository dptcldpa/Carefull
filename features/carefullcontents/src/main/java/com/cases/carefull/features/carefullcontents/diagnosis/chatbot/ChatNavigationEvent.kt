package com.cases.carefull.features.carefullcontents.diagnosis.chatbot

sealed class ChatNavigationEvent {
    data class ToHospitalList(val department: String) : ChatNavigationEvent()
    data class ToDiseaseInfo(val diseaseName: String) : ChatNavigationEvent()
}