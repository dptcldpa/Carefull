package com.cases.carefull.features.carefullcontents.feed.social

import com.cases.carefull.domain.model.Comment
import com.cases.carefull.domain.model.Post
import com.cases.carefull.domain.model.SocialCategory

data class SocialListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val selectedCategory: SocialCategory = SocialCategory.entries.first()
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
