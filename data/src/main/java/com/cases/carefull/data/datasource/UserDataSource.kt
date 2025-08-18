package com.cases.carefull.data.datasource

import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult

interface UserDataSource {
    // 사용자 정보 생성
    suspend fun create(user: UserInfo): DataResourceResult<Unit>
    // 사용자 정보 읽어오기
    suspend fun read(userId: String): DataResourceResult<UserInfo?>
    // 프로필 수정
    suspend fun update(user: UserInfo): DataResourceResult<Unit>
}