package com.cases.carefull.data.repository.feed.social

import com.cases.carefull.data.datasource.SocialCommentDataSource
import com.cases.carefull.data.dto.feed.CommentDto
import com.cases.carefull.domain.model.feed.Comment
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.domain.model.feed.FeedException
import com.cases.carefull.domain.repository.feed.SocialCommentRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.domain.util.toDataResourceResult
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SocialCommentRepositoryImpl @Inject constructor(
    private val dataSource: SocialCommentDataSource,
    private val auth: FirebaseAuth
) : SocialCommentRepository {
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "test"

    override suspend fun getComments(postId: String): DataResourceResult<List<Comment>> =
        runCatching {
            val dto = dataSource.getComments(postId)
            dto.map { it.toDomain() }
        }.toDataResourceResult()

    override suspend fun submitComment(
        postId: String,
        commentId: String?,
        content: String
    ): DataResourceResult<Unit> = runCatching {
        val userId = currentUserId
        if (commentId == null) {
            val newComment = CommentDto(
                postId = postId,
                userId = userId,
                content = content,
                createdAt = null,
                updatedAt = null
            )
            dataSource.createComment(postId, newComment)
        } else {
            val existingComment = dataSource.getComment(postId, commentId)
                ?: throw FeedException.NotFoundPost

            if (existingComment.userId != userId) {
                throw FeedException.Unauthorized
            }
            dataSource.updateComment(postId, commentId, content)
        }
        Unit
    }.toDataResourceResult()

    override suspend fun deleteComment(
        postId: String,
        commentId: String
    ): DataResourceResult<Unit> = runCatching {
        val userId = currentUserId

        val existingComment = dataSource.getComment(postId, commentId)
            ?: throw FeedException.NotFoundPost

        if (existingComment.userId != userId) {
            throw FeedException.Unauthorized
        }
        dataSource.deleteComment(postId, commentId)
        Unit
    }.toDataResourceResult()
}
