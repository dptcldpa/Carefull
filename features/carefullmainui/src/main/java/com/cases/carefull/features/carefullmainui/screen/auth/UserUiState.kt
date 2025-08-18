package com.cases.carefull.features.carefullmainui.screen.auth

import com.cases.carefull.domain.model.UserInfo

data class UserUiState (
    val userInfo: UserInfo? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)