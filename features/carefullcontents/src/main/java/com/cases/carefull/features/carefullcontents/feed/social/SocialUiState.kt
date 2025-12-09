package com.cases.carefull.features.carefullcontents.feed.social

import com.cases.carefull.domain.model.feed.Comment
import com.cases.carefull.domain.model.feed.Post
import com.cases.carefull.domain.model.feed.SocialCategory
import com.cases.carefull.features.carefullcontents.util.UiText

data class SocialListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isEndReached: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: UiText? = null,
    val selectedCategory: SocialCategory = SocialCategory.entries.first()
)

data class CreatePostUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: UiText? = null,
    val initialPost: Post? = null
)

data class PostDetailUiState(
    val existPost: Boolean = false,
    val isLoading: Boolean = false,
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val error: UiText? = null,
    val userLikedPost: Boolean = false,
    val isDeleted: Boolean = false,
    val currentUserId: String? = "test"
)
