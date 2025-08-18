package com.cases.carefull.data.datasource

import android.content.Context
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult
import com.kakao.sdk.user.model.User

interface KaKaoDataSource {
//    suspend fun login(context: Context): DataResourceResult<Unit>
//    suspend fun logout(): DataResourceResult<Unit>
//    suspend fun getTokenInfo(): DataResourceResult<AccessTokenInfo>
//    suspend fun getUserInfo(): DataResourceResult<User>

    suspend fun login(context: Context): DataResourceResult<UserInfo>
    suspend fun logout(): DataResourceResult<Unit>
    suspend fun isLoggedIn(): Boolean          // 자동 로그인 체크
    suspend fun getCurrentUser(): DataResourceResult<UserInfo> // 이미 로그인한 사용자 정보 가져오기
}