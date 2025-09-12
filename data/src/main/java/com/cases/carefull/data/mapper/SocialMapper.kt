package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.CommentDto
import com.cases.carefull.data.dto.PostDto
import com.cases.carefull.domain.model.Comment
import com.cases.carefull.domain.model.Post
import java.util.Date


fun PostDto.toDomain(): Post {
	return Post(
		id = this.id,
		title = this.title,
		content = this.content,
		category = this.category,
		imageUrl = this.image,
		userId = this.userId,
		createdAt = this.createdAt ?: Date(),
		likeCount = this.likeCount,
		commentCount = this.commentCount
	)
}

fun CommentDto.toDomain(): Comment {
	return Comment(
		id = this.id,
		postId = this.postId,
		userId = this.userId,
		content = this.content,
		createdAt = this.createdAt ?: Date()
	)
}

fun Post.toDto(): PostDto {
	return PostDto(
		id = this.id,
		title = this.title,
		content = this.content,
		category = this.category,
		image = this.imageUrl,
		userId = this.userId,
		likeCount = this.likeCount,
		commentCount = this.commentCount
	)
}

fun Comment.toDto(): CommentDto {
	return CommentDto(
		id = this.id,
		postId = this.postId,
		userId = this.userId,
		content = this.content
	)
}