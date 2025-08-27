package com.cases.carefull.data.firestore

import com.cases.carefull.data.datasource.UserDataSource
import com.cases.carefull.data.mapper.toDTO
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.model.UserDTO
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserDataSourceImpl: UserDataSource {

    private val db = Firebase.firestore

    override suspend fun createUser(user: UserInfo): DataResourceResult<Unit> = runCatching {
        val userDto = user.toDTO()

        db.collection("user_collection")
            .document(user.login_id)
            .set(userDto)
            .await()
    }.fold(
        onSuccess = { DataResourceResult.Success(Unit) },
        onFailure = { DataResourceResult.Error(it) }
    )

    override suspend fun readUser(userId: String): DataResourceResult<UserInfo?> = runCatching {
        val document = db.collection("user_collection").document(userId).get().await()
        val userDto = document.toObject(UserDTO::class.java)

        userDto?.toDomain(id=document.id)
    }.fold(
        onSuccess = { DataResourceResult.Success(it) },
        onFailure = { DataResourceResult.Error(it) }
    )

    override suspend fun updateUser(user: UserInfo): DataResourceResult<Unit> = runCatching {
        val updates = mapOf(
            "nickname" to user.nickname,
            "profile_image" to user.profile_image,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("user_collection")
            .document(user.login_id)
            .set(updates, SetOptions.merge())
            .await()
    }.fold(
        onSuccess = { DataResourceResult.Success(Unit) },
        onFailure = { DataResourceResult.Error(it) }
    )
}