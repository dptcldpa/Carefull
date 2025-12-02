package com.cases.carefull.data.dto.feed

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CommentDto(
	@DocumentId
	val id: String = "",
	val postId: String = "",
	val userId: String = "",
	val content: String = "",
	@ServerTimestamp
	val createdAt: Date? = null,
	@ServerTimestamp
	val updatedAt: Date? = null
)