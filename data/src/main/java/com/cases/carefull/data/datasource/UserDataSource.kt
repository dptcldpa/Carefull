package com.cases.carefull.data.datasource

import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult

interface UserDataSource {
    suspend fun createUser(user: UserInfo): DataResourceResult<Unit>
    suspend fun readUser(userId: String): DataResourceResult<UserInfo?>
    suspend fun updateUser(user: UserInfo): DataResourceResult<Unit>
}