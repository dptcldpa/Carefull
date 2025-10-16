package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.Comment
import com.cases.carefull.domain.model.Post
import com.cases.carefull.domain.util.BaseResult

interface SocialRepository {
	suspend fun getPosts(category: String): BaseResult<List<Post>>
	suspend fun getPostDetail(postId: String): BaseResult<Post>
	suspend fun createPost(
		title: String,
		content: String,
		category: String,
		imageUriString: String?
	): BaseResult<Unit>
	suspend fun updatePost(
		postId: String,
		title: String,
		content: String,
		category: String,
		imageUriString: String?
	): BaseResult<Unit>
	suspend fun deletePost(postId: String): BaseResult<Unit>
	suspend fun getComments(postId: String): BaseResult<List<Comment>>
	suspend fun addComment(postId: String, content: String): BaseResult<Unit>
	suspend fun deleteComment(postId: String, commentId: String): BaseResult<Unit>
	
	suspend fun updateComment(postId: String, commentId: String, newContent: String): BaseResult<Unit>
	
	suspend fun toggleLike(postId: String): BaseResult<Unit>
	suspend fun hasUserLikedPost(postId: String): BaseResult<Boolean>
}