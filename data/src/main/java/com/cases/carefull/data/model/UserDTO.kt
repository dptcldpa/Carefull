package com.cases.carefull.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserDTO(
    val nickname: String = "",
    val profile_image: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)