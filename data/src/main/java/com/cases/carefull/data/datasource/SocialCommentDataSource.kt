package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.feed.CommentDto
import com.cases.carefull.domain.model.feed.Comment
import com.cases.carefull.domain.util.DataResourceResult

interface SocialCommentDataSource {
    suspend fun getComments(postId: String): List<CommentDto>
    suspend fun getComment(postId: String, commentId: String): CommentDto?
    suspend fun createComment(postId: String, commentDto: CommentDto): Boolean
    suspend fun updateComment(postId: String, commentId: String, newContent: String): Boolean
    suspend fun deleteComment(postId: String, commentId: String): Boolean
}
