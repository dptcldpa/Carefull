package com.cases.carefull.data.mapper

import com.kakao.sdk.user.model.User
import com.cases.carefull.domain.model.UserInfo

fun User.toDomain(): UserInfo = UserInfo(
    login_id = this.id.toString(),
    nickname = this.kakaoAccount?.profile?.nickname.orEmpty(),
    profile_image = this.kakaoAccount?.profile?.profileImageUrl.orEmpty()
)