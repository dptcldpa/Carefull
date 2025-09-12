package com.cases.carefull.data.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PostDto(
	@DocumentId
	val id: String = "",
	val title: String = "",
	val content: String = "",
	val category: String = "",
	val image: String? = null,
	val userId: String = "",
	@ServerTimestamp
	val createdAt: Date? = null,
	@ServerTimestamp
	val updatedAt: Date? = null,
	
	val likeCount: Int = 0,
	val commentCount: Int = 0
)