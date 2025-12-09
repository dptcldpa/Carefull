package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.feed.PostDto
import com.cases.carefull.domain.model.feed.Post
import com.cases.carefull.domain.model.feed.SocialCategory
import com.cases.carefull.domain.util.DataResourceResult
import java.util.Date

interface SocialPostDataSource {
    suspend fun getPosts(
        category: String?,
        lastCreatedAt: Date? = null,
        lastId: String? = null
    ): List<PostDto>

    suspend fun getPostDetail(postId: String): PostDto?
    suspend fun createPost(postDto: PostDto): Boolean
    suspend fun updatePost(
        postId: String, updateFields: Map<String, Any?>
    ): Boolean

    suspend fun deletePost(postId: String): Boolean
    suspend fun toggleLike(postId: String, userId: String): Boolean
    suspend fun hasUserLikedPost(postId: String, userId: String): Boolean
}
