package com.cases.carefull.data.datasource

import android.content.Context
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult

interface KaKaoDataSource {
    suspend fun login(context: Context): DataResourceResult<UserInfo>
    suspend fun logout(): DataResourceResult<Unit>
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): DataResourceResult<UserInfo>
}