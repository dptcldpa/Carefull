package com.cases.carefull.data.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LikeDto(
	val userId: String = "",
	val postId: String = "",
	@ServerTimestamp
	val createdAt: Date? = null
)