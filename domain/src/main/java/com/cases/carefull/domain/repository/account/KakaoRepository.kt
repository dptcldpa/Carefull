package com.cases.carefull.domain.repository.account

import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface KakaoRepository {
    fun login(): Flow<DataResourceResult<UserInfo>>
    fun logout(): Flow<DataResourceResult<Unit>>
    fun isLoggedIn(): Flow<DataResourceResult<Boolean>>
    fun getCurrentUser(): Flow<DataResourceResult<UserInfo>>
}