package com.cases.carefull.data.repository.feed.social

import android.net.Uri
import androidx.core.net.toUri
import com.cases.carefull.data.datasource.SocialPostDataSource
import com.cases.carefull.data.dto.feed.PostDto
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.domain.model.feed.FeedException
import com.cases.carefull.domain.model.feed.Post
import com.cases.carefull.domain.model.feed.SocialCategory
import com.cases.carefull.domain.repository.feed.SocialPostRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.domain.util.toDataResourceResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class SocialPostRepositoryImpl @Inject constructor(
    private val socialPostDataSource: SocialPostDataSource,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : SocialPostRepository {

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "test"

    override suspend fun getPosts(
        category: SocialCategory,
        lastPost: Post?
    ): DataResourceResult<List<Post>> =
        runCatching {
            val categoryParam = if (category == SocialCategory.ALL) null else category.category

            val lastCreatedAt = lastPost?.createdAt
            val lastId = lastPost?.id

            val dto = socialPostDataSource.getPosts(
                category = categoryParam,
                lastCreatedAt = lastCreatedAt,
                lastId = lastId
            )
            dto.map { it.toDomain() }
        }.toDataResourceResult()

    override suspend fun getPostDetail(postId: String): DataResourceResult<Post> =
        runCatching {
            val postDto = socialPostDataSource.getPostDetail(postId)
            val validDto = postDto ?: throw FeedException.NotFoundPost
            validDto.toDomain()
        }.toDataResourceResult()

    override suspend fun submitPost(
        postId: String?,
        title: String,
        content: String,
        category: SocialCategory,
        imageUriString: String?
    ): DataResourceResult<Unit> = runCatching {
        val userId = currentUserId
        val finalImageUrl = if (imageUriString != null) {
            resolveImageString(imageUriString)
        } else {
            null
        }

        if (postId == null) {
            val newPost = PostDto(
                userId = userId,
                title = title,
                content = content,
                category = category.category,
                image = finalImageUrl,
                createdAt = null,
                updatedAt = null,
                likeCount = 0,
                commentCount = 0
            )
            socialPostDataSource.createPost(newPost)

        } else {
            val existingPost = socialPostDataSource.getPostDetail(postId)
                ?: throw FeedException.NotFoundPost

            if (existingPost.userId != userId) {
                throw FeedException.Unauthorized
            }

            val updates = mutableMapOf<String, Any?>(
                "title" to title,
                "content" to content,
                "category" to category.category,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (finalImageUrl != null) {
                updates["image"] = finalImageUrl
            }
            socialPostDataSource.updatePost(postId, updates)
        }
        Unit
    }.toDataResourceResult()


    private suspend fun resolveImageString(uriString: String): String {
        return if (uriString.startsWith("http")) {
            uriString
        } else {
            uploadImageToStorage(uriString.toUri())
        }
    }

    private suspend fun uploadImageToStorage(uri: Uri): String {
        val fileName = "social_images/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

    override suspend fun deletePost(postId: String): DataResourceResult<Unit> = runCatching {
        val postDto = socialPostDataSource.getPostDetail(postId)
            ?: throw FeedException.NotFoundPost

        if (postDto.userId != currentUserId) {
            throw FeedException.Unauthorized
        }

        if (!postDto.image.isNullOrBlank()) {
            try {
                storage.getReferenceFromUrl(postDto.image).delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        socialPostDataSource.deletePost(postId)
        Unit
    }.toDataResourceResult()

    override suspend fun toggleLike(postId: String): DataResourceResult<Unit> = runCatching {
        val userId = currentUserId
        socialPostDataSource.toggleLike(postId, userId)
        Unit
    }.toDataResourceResult()

    override suspend fun hasUserLikedPost(postId: String): DataResourceResult<Boolean> =
        runCatching {
            val userId = currentUserId

            socialPostDataSource.hasUserLikedPost(postId, userId)
        }.toDataResourceResult()
}
