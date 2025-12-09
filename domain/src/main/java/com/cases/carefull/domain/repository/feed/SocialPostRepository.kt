package com.cases.carefull.domain.repository.feed

import com.cases.carefull.domain.model.feed.Post
import com.cases.carefull.domain.model.feed.SocialCategory
import com.cases.carefull.domain.util.DataResourceResult

interface SocialPostRepository {
    suspend fun getPosts(
        category: SocialCategory,
        lastPost: Post? = null
    ): DataResourceResult<List<Post>>

    suspend fun getPostDetail(postId: String): DataResourceResult<Post>

    suspend fun submitPost(
        postId: String? = null,
        title: String,
        content: String,
        category: SocialCategory,
        imageUriString: String?
    ): DataResourceResult<Unit>

    suspend fun deletePost(postId: String): DataResourceResult<Unit>

    suspend fun toggleLike(postId: String): DataResourceResult<Unit>

    suspend fun hasUserLikedPost(postId: String): DataResourceResult<Boolean>
}
