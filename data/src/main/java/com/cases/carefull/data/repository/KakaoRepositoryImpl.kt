package com.cases.carefull.data.repository

import android.content.Context
import com.cases.carefull.data.datasource.KaKaoDataSource
import com.cases.carefull.data.datasource.UserDataSource
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.repository.KakaoRepository
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class KakaoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val kakaoDataSource: KaKaoDataSource,
    private val userDataSource: UserDataSource
) : KakaoRepository {
    override fun login(): Flow<DataResourceResult<UserInfo>> = flow {
        emit(DataResourceResult.Loading)

        val userInfo = kakaoDataSource.login(context)
        userDataSource.createUser(userInfo)
        emit(DataResourceResult.Success(userInfo))
    }
    .catch { emit(DataResourceResult.Error(it)) }
    .flowOn(Dispatchers.IO)

    override fun logout(): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading)

        kakaoDataSource.logout()
        emit(DataResourceResult.Success(Unit))
    }
    .catch { emit(DataResourceResult.Error(it)) }
    .flowOn(Dispatchers.IO)

    override fun isLoggedIn(): Flow<DataResourceResult<Boolean>> = flow {
        emit(DataResourceResult.Loading)

        val result = kakaoDataSource.isLoggedIn()
        emit(DataResourceResult.Success(result))
    }
    .catch { emit(DataResourceResult.Error(it)) }
    .flowOn(Dispatchers.IO)

    override fun getCurrentUser(): Flow<DataResourceResult<UserInfo>> = flow {
        emit(DataResourceResult.Loading)

        val userInfo = kakaoDataSource.getCurrentUser()
        emit(DataResourceResult.Success(userInfo))
    }
    .catch { emit(DataResourceResult.Error(it)) }
    .flowOn(Dispatchers.IO)
}