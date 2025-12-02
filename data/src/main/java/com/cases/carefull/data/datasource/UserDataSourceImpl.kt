package com.cases.carefull.data.datasource

import com.cases.carefull.data.dto.UserDTO
import com.cases.carefull.data.mapper.toDTO
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor() : UserDataSource {

    private val db = Firebase.firestore

    override suspend fun createUser(user: UserInfo) {
        val userDto = user.toDTO()
        db.collection("user_collection")
            .document(user.login_id)
            .set(userDto)
            .await()
    }

    override suspend fun readUser(userId: String): UserInfo? {
        val document = db.collection("user_collection").document(userId).get().await()
        val userDto = document.toObject(UserDTO::class.java)
        return userDto?.toDomain(id = document.id)
    }

    override suspend fun updateUser(user: UserInfo) {
        val updates = mapOf(
            "nickname" to user.nickname,
            "profile_image" to user.profile_image,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("user_collection")
            .document(user.login_id)
            .set(updates, SetOptions.merge())
            .await()
    }
}