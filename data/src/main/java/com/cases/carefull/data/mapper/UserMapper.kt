package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.account.UserDto
import com.cases.carefull.domain.model.UserInfo

fun UserDto.toDomain(id:String): UserInfo = UserInfo(
    login_id = id,
    nickname = this.nickname,
    profile_image = this.profile_image
)

fun UserInfo.toDTO(): UserDto = UserDto(
    nickname = this.nickname,
    profile_image = this.profile_image
)