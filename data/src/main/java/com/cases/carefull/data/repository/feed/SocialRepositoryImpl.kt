package com.cases.carefull.data.repository.feed

import android.net.Uri
import com.cases.carefull.data.dto.feed.PostDto
import com.cases.carefull.domain.model.feed.Comment
import com.cases.carefull.domain.model.feed.Post
import com.cases.carefull.domain.repository.feed.SocialRepository
import com.cases.carefull.domain.util.BaseResult
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.dto.feed.CommentDto
import com.cases.carefull.data.dto.feed.LikeDto
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import androidx.core.net.toUri
import com.cases.carefull.data.constant.FirestoreCollection
import com.cases.carefull.domain.model.feed.SocialCategory

class SocialRepositoryImpl @Inject constructor(
) : SocialRepository {
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth
    private val userId = getCurrentUserId()
    private val postCollection = firestore.collection("posts")

    private fun getCurrentUserId(): String? {
//		return auth.currentUser?.uid
        return "test"
    }

    private suspend fun uploadImageToStorage(uri: Uri): String {
        val fileName = "social_images/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }


    override suspend fun getPosts(category: SocialCategory): BaseResult<List<Post>> {
        return try {
            val query = if (category == SocialCategory.ALL) {
                postCollection.orderBy(FirestoreCollection.CREATED_AT, Query.Direction.DESCENDING)
            } else {
                postCollection.whereEqualTo(FirestoreCollection.CATEGORY, category.category)
                    .orderBy(FirestoreCollection.CREATED_AT, Query.Direction.DESCENDING)
            }

            val documents = query.get().await()
            val posts = documents.toObjects(PostDto::class.java).map { it.toDomain() }
            BaseResult.Success(posts)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }

    override suspend fun getPostDetail(postId: String): BaseResult<Post> {
        return try {
            val document = postCollection.document(postId).get().await()
            val postDto = document.toObject(PostDto::class.java)
                ?: return BaseResult.Error(Exception("Post not found"))
            BaseResult.Success(postDto.toDomain())
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }

    override suspend fun createPost(
        title: String,
        content: String,
        category: SocialCategory,
        imageUriString: String?
    ): BaseResult<Unit> {
        return try {
            val userId =
                userId ?: return BaseResult.Error(Exception("User not logged in"))
            var imageUrl: String? = null
            if (imageUriString != null) {
                val imageUri = imageUriString.toUri()
                imageUrl = uploadImageToStorage(imageUri)
            }
            val postDto = PostDto(
                title = title,
                content = content,
                category = category.category,
                image = imageUrl,
                userId = userId
            )
            postCollection.add(postDto).await()
            BaseResult.Success(Unit)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }

    override suspend fun updatePost(
        postId: String,
        title: String,
        content: String,
        category: SocialCategory,
        imageUriString: String?
    ): BaseResult<Unit> {
        return try {
            val userId =
                userId ?: return BaseResult.Error(Exception("User not logged in"))
            val postRef = postCollection.document(postId)
            val existingPost = postRef.get().await().toObject(PostDto::class.java)

            if (existingPost == null || existingPost.userId != userId) {
                return BaseResult.Error(Exception("Post not found or unauthorized"))
            }

            var imageUrl: String? = existingPost.image

            if (imageUriString != null) {
                val imageUri = imageUriString.toUri()
                imageUrl = uploadImageToStorage(imageUri)
            }

            val updates = mapOf(
                "title" to title,
                "content" to content,
                "category" to category.category,
                "image" to imageUrl,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            postRef.update(updates).await()
            BaseResult.Success(Unit)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }


    override suspend fun deletePost(postId: String): BaseResult<Unit> {
        return try {
            val userId =
                userId ?: return BaseResult.Error(Exception("User not logged in"))
            val postRef = postCollection.document(postId)
            val existingPost = postRef.get().await().toObject(PostDto::class.java)

            if (existingPost == null || existingPost.userId != userId) {
                return BaseResult.Error(Exception("Post not found or unauthorized"))
            }

            val commentsToDelete = if (existingPost.commentCount > 0) {
                postRef.collection(FirestoreCollection.COMMENTS).get().await()
            } else {
                null
            }

            val likesToDelete = if (existingPost.likeCount > 0) {
                postRef.collection(FirestoreCollection.LIKES).get().await()
            } else {
                null
            }

            if (!existingPost.image.isNullOrBlank()) {
                try {
                    val storageRef = storage.getReferenceFromUrl(existingPost.image)
                    storageRef.delete().await()
                } catch (e: Exception) {
                    println("Error deleting image from Storage: ${e.message}")
                }
            }
            firestore.runTransaction { transaction ->
                commentsToDelete?.documents?.forEach { doc ->
                    transaction.delete(doc.reference)
                }
                likesToDelete?.documents?.forEach { doc ->
                    transaction.delete(doc.reference)
                }
                transaction.delete(postRef)
            }.await()

            BaseResult.Success(Unit)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }

    override suspend fun getComments(postId: String): BaseResult<List<Comment>> {
        return try {
            val commentsCollection = postCollection.document(postId).collection(FirestoreCollection.COMMENTS)
            val documents =
                commentsCollection.orderBy(FirestoreCollection.CREATED_AT, Query.Direction.ASCENDING).get().await()
            val comments = documents.toObjects(CommentDto::class.java).map { it.toDomain() }
            BaseResult.Success(comments)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }

    override suspend fun addComment(postId: String, content: String): BaseResult<Unit> {
        return try {
            val userId =
                userId ?: return BaseResult.Error(Exception("User not logged in"))
            val commentDto = CommentDto(
                postId = postId,
                userId = userId,
                content = content
            )
            firestore.runTransaction { transaction ->
                val postRef = postCollection.document(postId)
                val newCommentRef = postRef.collection(FirestoreCollection.COMMENTS).document()
                transaction.set(newCommentRef, commentDto)
                transaction.update(postRef, FirestoreCollection.COMMENT_COUNT, FieldValue.increment(1))
            }.await()

            BaseResult.Success(Unit)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }

    override suspend fun deleteComment(postId: String, commentId: String): BaseResult<Unit> {
        return try {
            val userId =
                userId ?: return BaseResult.Error(Exception("User not logged in"))
            val commentRef =
                postCollection.document(postId).collection(FirestoreCollection.COMMENTS).document(commentId)
            val existingComment = commentRef.get().await().toObject(CommentDto::class.java)

            if (existingComment == null || existingComment.userId != userId) {
                return BaseResult.Error(Exception("Comment not found or unauthorized"))
            }
            firestore.runTransaction { transaction ->
                val postRef = postCollection.document(postId)
                transaction.delete(commentRef)
                transaction.update(postRef, FirestoreCollection.COMMENT_COUNT, FieldValue.increment(-1))
            }.await()

            BaseResult.Success(Unit)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }

    override suspend fun toggleLike(postId: String): BaseResult<Unit> {
        return try {
            val userId =
                userId ?: return BaseResult.Error(Exception("User not logged in"))
            val postRef = postCollection.document(postId)
            val likeRef = postRef.collection(FirestoreCollection.LIKES).document(userId)

            firestore.runTransaction { transaction ->
                val likeSnapshot = transaction.get(likeRef)

                if (likeSnapshot.exists()) {
                    transaction.delete(likeRef)
                    transaction.update(postRef, FirestoreCollection.LIKES_COUNT, FieldValue.increment(-1))
                } else {
                    val likeDto = LikeDto(
                        postId = postId,
                        userId = userId
                    )
                    transaction.set(likeRef, likeDto)
                    transaction.update(postRef, FirestoreCollection.LIKES_COUNT, FieldValue.increment(1))
                }
            }.await()
            BaseResult.Success(Unit)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }

    override suspend fun hasUserLikedPost(postId: String): BaseResult<Boolean> {
        return try {
            val userId =
                userId ?: return BaseResult.Error(Exception("User not logged in"))
            val likeRef = postCollection.document(postId).collection(FirestoreCollection.LIKES).document(userId)
            val snapshot = likeRef.get().await()
            BaseResult.Success(snapshot.exists())
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }

    override suspend fun updateComment(
        postId: String,
        commentId: String,
        newContent: String
    ): BaseResult<Unit> {
        return try {
            val userId =
                userId ?: return BaseResult.Error(Exception("User not logged in"))
            val commentRef =
                postCollection.document(postId).collection(FirestoreCollection.COMMENTS).document(commentId)
            val existingComment = commentRef.get().await().toObject(CommentDto::class.java)

            if (existingComment == null || existingComment.userId != userId) {
                return BaseResult.Error(Exception("Comment not found or unauthorized"))
            }
            commentRef.update(
                mapOf(
                    FirestoreCollection.CONTENT to newContent,
                    FirestoreCollection.UPDATED_AT to FieldValue.serverTimestamp()
                )
            ).await()

            BaseResult.Success(Unit)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }
}