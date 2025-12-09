package com.cases.carefull.data.datasource

import com.cases.carefull.data.constant.FirestoreCollection
import com.cases.carefull.data.dto.feed.LikeDto
import com.cases.carefull.data.dto.feed.PostDto
import com.cases.carefull.domain.util.FeedConfig
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class SocialPostDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : SocialPostDataSource {

    override suspend fun getPosts(
        category: String?,
        lastCreatedAt: Date?,
        lastId: String?
    ): List<PostDto> {
        val collection = firestore.collection(FirestoreCollection.POSTS)

        val baseQuery = if (category != null) {
            collection.whereEqualTo(FirestoreCollection.CATEGORY, category)
        } else {
            collection
        }

        var finalQuery = baseQuery
            .orderBy(FirestoreCollection.CREATEDAT, Query.Direction.DESCENDING)
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
            .limit(FeedConfig.POST_PAGING_SIZE)
        if (lastCreatedAt != null && lastId != null) {
            finalQuery = finalQuery.startAfter(lastCreatedAt, lastId)
        }

        return finalQuery
            .get()
            .await()
            .toObjects(PostDto::class.java)
    }

    override suspend fun getPostDetail(postId: String): PostDto? {
        val collection = firestore.collection(FirestoreCollection.POSTS)
            .document(postId)

        return collection
            .get()
            .await()
            .toObject(PostDto::class.java)
    }

    override suspend fun createPost(postDto: PostDto): Boolean {
        firestore.collection(FirestoreCollection.POSTS)
            .add(postDto)
            .await()
        return true
    }

    override suspend fun updatePost(
        postId: String,
        updateFields: Map<String, Any?>
    ): Boolean {
        firestore.collection(FirestoreCollection.POSTS)
            .document(postId)
            .update(updateFields)
            .await()
        return true
    }

    override suspend fun deletePost(postId: String): Boolean {
        val postRef = firestore.collection(FirestoreCollection.POSTS)
            .document(postId)

        val comments = postRef.collection(FirestoreCollection.COMMENTS).get().await().documents
        val likes = postRef.collection(FirestoreCollection.LIKES).get().await().documents
        val allSubDocuments = comments + likes

        allSubDocuments.chunked(450).forEach { batchList ->
            firestore.runBatch { batch ->
                batchList.forEach { doc -> batch.delete(doc.reference) }
            }.await()
        }

        postRef.delete().await()

        return true
    }

    override suspend fun toggleLike(postId: String, userId: String): Boolean {
        val postRef = firestore.collection(FirestoreCollection.POSTS).document(postId)
        val likeRef = postRef.collection(FirestoreCollection.LIKES).document(userId)

        firestore.runTransaction { transaction ->
            val likeSnapshot = transaction.get(likeRef)

            if (likeSnapshot.exists()) {
                transaction.delete(likeRef)
                transaction.update(
                    postRef,
                    FirestoreCollection.LIKECOUNT,
                    FieldValue.increment(-1)
                )
            } else {
                val likeDto = LikeDto(
                    postId = postId,
                    userId = userId,
                )
                transaction.set(likeRef, likeDto)
                transaction.update(
                    postRef,
                    FirestoreCollection.LIKECOUNT,
                    FieldValue.increment(1)
                )
            }
        }.await()

        return true
    }

    override suspend fun hasUserLikedPost(postId: String, userId: String): Boolean {
        val snapshot = firestore.collection(FirestoreCollection.POSTS)
            .document(postId)
            .collection(FirestoreCollection.LIKES)
            .document(userId)
            .get()
            .await()

        return snapshot.exists()
    }
}
