package com.cases.carefull.data.datasource

import com.cases.carefull.data.constant.FirestoreCollection
import com.cases.carefull.data.dto.feed.CommentDto
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SocialCommentDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SocialCommentDataSource {

    override suspend fun getComments(postId: String): List<CommentDto> {
        return firestore.collection(FirestoreCollection.POSTS)
            .document(postId)
            .collection(FirestoreCollection.COMMENTS)
            .orderBy(FirestoreCollection.CREATEDAT, Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(CommentDto::class.java)
    }

    override suspend fun getComment(postId: String, commentId: String): CommentDto? {
        return firestore.collection(FirestoreCollection.POSTS)
            .document(postId)
            .collection(FirestoreCollection.COMMENTS)
            .document(commentId)
            .get()
            .await()
            .toObject(CommentDto::class.java)
    }

    override suspend fun createComment(postId: String, commentDto: CommentDto): Boolean {
        val postRef = firestore.collection(FirestoreCollection.POSTS).document(postId)
        val commentRef = postRef.collection(FirestoreCollection.COMMENTS).document()

        firestore.runTransaction { transaction ->
            val newDto = commentDto.copy(id = commentRef.id)
            transaction.set(commentRef, newDto)
            transaction.update(postRef, FirestoreCollection.COMMENTCOUNT, FieldValue.increment(1))
        }.await()
        return true
    }

    override suspend fun updateComment(postId: String, commentId: String, newContent: String): Boolean {
        firestore.collection(FirestoreCollection.POSTS)
            .document(postId)
            .collection(FirestoreCollection.COMMENTS)
            .document(commentId)
            .update(
                mapOf(
                    FirestoreCollection.CONTENT to newContent,
                    FirestoreCollection.UPDATEDAT to FieldValue.serverTimestamp()
                )
            ).await()
        return true
    }

    override suspend fun deleteComment(postId: String, commentId: String): Boolean {
        val postRef = firestore.collection(FirestoreCollection.POSTS).document(postId)
        val commentRef = postRef.collection(FirestoreCollection.COMMENTS).document(commentId)

        firestore.runTransaction { transaction ->
            transaction.delete(commentRef)
            transaction.update(postRef, FirestoreCollection.COMMENTCOUNT, FieldValue.increment(-1))
        }.await()
        return true
    }
}
