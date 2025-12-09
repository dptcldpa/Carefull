package com.cases.carefull.domain.repository.feed

import com.cases.carefull.domain.model.feed.Comment
import com.cases.carefull.domain.util.DataResourceResult

interface SocialCommentRepository {
    suspend fun getComments(postId: String): DataResourceResult<List<Comment>>

    suspend fun submitComment(
        postId: String,
        commentId: String? = null,
        content: String
    ): DataResourceResult<Unit>

    suspend fun deleteComment(postId: String, commentId: String): DataResourceResult<Unit>
}
