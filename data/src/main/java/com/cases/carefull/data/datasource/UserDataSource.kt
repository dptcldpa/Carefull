package com.cases.carefull.data.datasource

import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult

interface UserDataSource {
    suspend fun createUser(user: UserInfo)
    suspend fun readUser(userId: String): UserInfo?
    suspend fun updateUser(user: UserInfo)
}