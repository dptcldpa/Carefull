package com.cases.carefull.data.mapper

import com.cases.carefull.data.model.UserDTO
import com.cases.carefull.domain.model.UserInfo
import com.google.firebase.Timestamp

fun UserDTO.toDomain(): UserInfo = UserInfo(
    login_id = this.login_id,
    nickname = this.nickname,
    profile_image = this.profile_image
)

fun UserInfo.toDTO(): UserDTO = UserDTO(
    login_id = this.login_id,
    nickname = this.nickname,
    profile_image = this.profile_image
//    createdAt = Timestamp.now(),
//    updatedAt = Timestamp.now()
)