package com.cases.carefull.domain.model

import java.util.Date

data class Comment(
	val id: String,
	val postId: String,
	val userId: String,
	val content: String,
	val createdAt: Date
)