package com.cases.carefull.data.dto.account

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserDto(
    val nickname: String = "",
    val profile_image: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)