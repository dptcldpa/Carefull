package com.cases.carefull.data.repository

import android.content.Context
import android.util.Log
import com.cases.carefull.data.datasource.KaKaoDataSource
import com.cases.carefull.data.datasource.UserDataSource
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val context: Context,
    private val kakaoDataSource: KaKaoDataSource,
    private val userDataSource: UserDataSource
) : UserRepository {
    override suspend fun getUser(userId: String): Flow<DataResourceResult<UserInfo?>> = flow {//
        emit(DataResourceResult.Loading)
        emit(userDataSource.readUser(userId))
    }.catch {
        emit(DataResourceResult.Error(it))
    }

    override suspend fun updateUser(user: UserInfo): Flow<DataResourceResult<Unit>> = flow {//
        emit(DataResourceResult.Loading)
        emit(userDataSource.updateUser(user))
    }.catch {
        emit(DataResourceResult.Error(it))
    }

    override suspend fun getCurrentUser(): Flow<DataResourceResult<UserInfo>> = flow {
        emit(DataResourceResult.Loading)
        val kakaoUserResult = kakaoDataSource.getCurrentUser()
        if (kakaoUserResult is DataResourceResult.Success) {
            userDataSource.createUser(kakaoUserResult.data)
        }
        emit(kakaoUserResult)
    }.catch {
        emit(DataResourceResult.Error(it))
    }

    override suspend fun login(): Flow<DataResourceResult<UserInfo>> = flow {
        emit(DataResourceResult.Loading)

        when (val kakaoResult = kakaoDataSource.login(context)) {
            is DataResourceResult.Success -> {
                val userInfo = kakaoResult.data
                when (val firestoreResult = userDataSource.createUser(userInfo)) {
                    is DataResourceResult.Success -> {
                        emit(DataResourceResult.Success(userInfo))
                    }
                    is DataResourceResult.Error -> {
                        emit(DataResourceResult.Error(firestoreResult.exception))
                    }
                    DataResourceResult.Loading -> {}
                }
            }
            is DataResourceResult.Error -> {
                emit(DataResourceResult.Error(kakaoResult.exception))
            }
            DataResourceResult.Loading -> {}
        }
    }.catch { emit(DataResourceResult.Error(it)) }

    override suspend fun logout(): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading)
        emit(kakaoDataSource.logout())
    }.catch {
        emit(DataResourceResult.Error(it))
    }

    override suspend fun isLoggedIn(): Flow<DataResourceResult<Boolean>> = flow {
        emit(DataResourceResult.Loading)
        val result = kakaoDataSource.isLoggedIn()
        emit(DataResourceResult.Success(result))
    }.catch {
        emit(DataResourceResult.Error(it))
    }
}