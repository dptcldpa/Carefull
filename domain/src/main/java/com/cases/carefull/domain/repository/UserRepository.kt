package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(): Flow<DataResourceResult<UserInfo>>
    suspend fun logout(): Flow<DataResourceResult<Unit>>
    suspend fun isLoggedIn(): Flow<DataResourceResult<Boolean>>

    suspend fun getUser(userId: String): Flow<DataResourceResult<UserInfo?>>
    suspend fun updateUser(user: UserInfo): Flow<DataResourceResult<Unit>>
    suspend fun getCurrentUser(): Flow<DataResourceResult<UserInfo>>
}