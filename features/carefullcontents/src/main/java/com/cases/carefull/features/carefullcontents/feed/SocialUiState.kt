package com.cases.carefull.features.carefullcontents.feed

import com.cases.carefull.domain.model.Comment
import com.cases.carefull.domain.model.Post

data class SocialListUiState(
	val isLoading: Boolean = false,
	val isRefreshing: Boolean = false,
	val posts: List<Post> = emptyList(),
	val error: String? = null,
	val selectedCategory: String = "전체"
)


data class CreatePostUiState(
	val isLoading: Boolean = false,
	val isSuccess: Boolean = false,
	val error: String? = null,
	val initialPost: Post? = null
)

data class PostDetailUiState(
	val existPost: Boolean = false,
	val isLoading: Boolean = false,
	val post: Post? = null,
	val comments: List<Comment> = emptyList(),
	val error: String? = null,
	val userLikedPost: Boolean = false,
	val isDeleted: Boolean = false,
	val currentUserId: String? = "test"
)
