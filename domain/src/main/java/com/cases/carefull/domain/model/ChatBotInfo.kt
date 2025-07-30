package com.cases.carefull.domain.model

data class ChatBotInfo(
    val message: String,
    val isUser: Boolean,
    val clickableDepartments: List<String>? = null,
    val onClickDepartment: ((String) -> Unit)? = null
)