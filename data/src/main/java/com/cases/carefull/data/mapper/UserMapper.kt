package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.UserDTO
import com.cases.carefull.domain.model.UserInfo

fun UserDTO.toDomain(id:String): UserInfo = UserInfo(
    login_id = id,
    nickname = this.nickname,
    profile_image = this.profile_image
)

fun UserInfo.toDTO(): UserDTO = UserDTO(
    nickname = this.nickname,
    profile_image = this.profile_image
)