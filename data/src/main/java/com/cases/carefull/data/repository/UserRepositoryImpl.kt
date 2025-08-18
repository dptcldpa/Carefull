package com.cases.carefull.data.repository

import android.content.Context
import com.cases.carefull.data.datasource.KaKaoDataSource
import com.cases.carefull.data.datasource.UserDataSource
import com.cases.carefull.data.mapper.toDTO
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val kakaoDataSource: KaKaoDataSource,
    private val userDataSource: UserDataSource
) : UserRepository { // datasource

    override suspend fun createUser(user: UserInfo): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading) // 로딩 상태 먼저 emit
        emit(userDataSource.create(user)) // 실제 데이터 처리
    }.catch{
        emit(DataResourceResult.Error(it)) // Flow 내부에서 예외 처리
    }

    override suspend fun getUser(userId: String): Flow<DataResourceResult<UserInfo?>> = flow {
        emit(DataResourceResult.Loading)
        emit(userDataSource.read(userId))
    }.catch {
        emit(DataResourceResult.Error(it))
    }

    override suspend fun updateUser(user: UserInfo): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading)
        emit(userDataSource.update(user))
    }.catch {
        emit(DataResourceResult.Error(it))
    }

    // Kakao 로그인 처리
    suspend fun loginWithKakao(context: Context): Flow<DataResourceResult<UserInfo>> = flow {
        emit(DataResourceResult.Loading)
        val result = kakaoDataSource.login(context)
        if (result is DataResourceResult.Success) {
            // Firestore에 저장
            userDataSource.create(result.data)
        }
        emit(result)
    }.catch { emit(DataResourceResult.Error(it)) }

    suspend fun logout(): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading)
        val result = kakaoDataSource.logout()
        emit(result)
    }.catch { emit(DataResourceResult.Error(it)) }
}

//private val db = Firebase.firestore
//
//override suspend fun saveUser(userInfo: UserInfo): DataResourceResult<Unit> { // flow로 감싸기
//    return runCatching { // emit
//        val entity = userInfo.toDTO()
//        db.collection("user_collection")
//            .document(userInfo.login_id)
//            .set(entity.copy(
//                updatedAt = com.google.firebase.Timestamp.now(),
//                createdAt = com.google.firebase.Timestamp.now()
//            ))
//            .await()
//    }.let { result ->
//        result.fold(
//            onSuccess = { DataResourceResult.Success(Unit) },
//            onFailure = { e -> DataResourceResult.Error(e) }
//        )
//    }
//}
