package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
//    suspend fun saveUser(userInfo: UserInfo): DataResourceResult<Unit> // flow
    suspend fun createUser(user: UserInfo): Flow<DataResourceResult<Unit>>
    suspend fun getUser(userId: String): Flow<DataResourceResult<UserInfo?>>
    suspend fun updateUser(user: UserInfo): Flow<DataResourceResult<Unit>>

}

//// "로그인 상태 확인해줘"
//suspend fun checkLoginStatus(): Flow<DataResourceResult<UserInfo>>
//// "카카오로 로그인 시켜줘"
//suspend fun loginWithKakao(): Flow<DataResourceResult<UserInfo>>
//// "로그아웃 시켜줘"
//suspend fun logout(): Flow<DataResourceResult<Unit>>