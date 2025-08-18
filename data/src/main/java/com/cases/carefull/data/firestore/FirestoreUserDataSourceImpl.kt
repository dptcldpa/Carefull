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

class FirestoreUserDataSourceImpl: UserDataSource {

    private val db = Firebase.firestore

    override suspend fun create(user: UserInfo): DataResourceResult<Unit> = runCatching {
        val userDto = user.toDTO()

        db.collection("user_collection")
            .document(userDto.login_id)
            .set(userDto)
            .await()
    }.fold(
        onSuccess = { DataResourceResult.Success(Unit) },
        onFailure = { DataResourceResult.Error(it) }
    )

    override suspend fun read(userId: String): DataResourceResult<UserInfo?> = runCatching {
        val document = db.collection("user_collection").document(userId).get().await()
        val userDto = document.toObject(UserDTO::class.java)

        userDto?.toDomain()
    }.fold(
        onSuccess = { DataResourceResult.Success(it) },
        onFailure = { DataResourceResult.Error(it) }
    )

    override suspend fun update(user: UserInfo): DataResourceResult<Unit> = runCatching {
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

//override suspend fun create(user: UserInfo): DataResourceResult<Unit> {
//    // runCatching은 람다 블록을 실행하고, 그 결과를 Result 객체로 감싸서 반환합니다.
//    // 성공하면 Result.success(결과), 실패(예외 발생)하면 Result.failure(예외)를 반환합니다.
//    return runCatching {
//        db.collection("users").document(user.login_id).set(user).await()
//    }.fold(
//        // fold 함수는 Result 객체를 열어서 성공/실패에 따라 다른 처리를 하도록 합니다.
//        onSuccess = { DataResourceResult.Success(Unit) }, // 성공 시, Unit을 Success로 감싸서 반환
//        onFailure = { exception -> DataResourceResult.Error(exception) } // 실패 시, 예외를 Error로 감싸서 반환
//    )
//}
//
//override suspend fun read(userId: String): DataResourceResult<UserInfo?> {
//    return runCatching {
//        val document = db.collection("users").document(userId).get().await()
//        document.toObject(UserInfo::class.java) // 이 값이 성공 시의 결과가 됨
//    }.fold(
//        onSuccess = { user -> DataResourceResult.Success(user) }, // 성공 시, user(UserInfo? 타입)를 Success로 감싸서 반환
//        onFailure = { exception -> DataResourceResult.Error(exception) }
//    )
//}
//
//override suspend fun update(user: UserInfo): DataResourceResult<Unit> {
//    return runCatching {
//        db.collection("users").document(user.login_id).set(user).await()
//    }.fold(
//        onSuccess = { DataResourceResult.Success(Unit) },
//        onFailure = { exception -> DataResourceResult.Error(exception) }
//    )
//}