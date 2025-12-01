package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(userId: String): Flow<DataResourceResult<UserInfo?>>
    fun updateUser(user: UserInfo): Flow<DataResourceResult<Unit>>
}