package com.cases.carefull.data.repository

import android.content.Context
import com.cases.carefull.data.datasource.KaKaoDataSource
import com.cases.carefull.data.datasource.UserDataSource
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
	@ApplicationContext private val context: Context,
	private val kakaoDataSource: KaKaoDataSource,
	private val userDataSource: UserDataSource
) : UserRepository {
	override suspend fun getUser(userId: String): Flow<DataResourceResult<UserInfo?>> = flow {//
		emit(DataResourceResult.Loading)
		emit(userDataSource.readUser(userId))
	}.flowOn(Dispatchers.IO).catch { emit(DataResourceResult.Error(it)) }
	
	override suspend fun updateUser(user: UserInfo): Flow<DataResourceResult<Unit>> = flow {//
		emit(DataResourceResult.Loading)
		emit(userDataSource.updateUser(user))
	}.flowOn(Dispatchers.IO).catch { emit(DataResourceResult.Error(it)) }
	
	override suspend fun getCurrentUser(): Flow<DataResourceResult<UserInfo>> = flow {
		emit(DataResourceResult.Loading)
		
		when (val kakaoResult = kakaoDataSource.getCurrentUser()) {
			is DataResourceResult.Success -> {
				val userInfo = kakaoResult.data
				when (val backendResult = userDataSource.createUser(userInfo)) {
					is DataResourceResult.Success -> emit(DataResourceResult.Success(userInfo))
					is DataResourceResult.Error -> emit(backendResult)
					DataResourceResult.Loading -> {}
				}
			}
			
			is DataResourceResult.Error -> emit(kakaoResult)
			DataResourceResult.Loading -> {}
		}
	}.flowOn(Dispatchers.IO).catch { emit(DataResourceResult.Error(it)) }
	
	override suspend fun login(): Flow<DataResourceResult<UserInfo>> = flow {
		emit(DataResourceResult.Loading)
		
		val kakaoResult = kakaoDataSource.login(context)
		
		if (kakaoResult is DataResourceResult.Success) {
			val userInfo = kakaoResult.data
			val backendResult = userDataSource.createUser(userInfo)
			if (backendResult is DataResourceResult.Success) {
				emit(DataResourceResult.Success(userInfo))
			} else if (backendResult is DataResourceResult.Error) {
				emit(backendResult)
			}
		} else if (kakaoResult is DataResourceResult.Error) {
			emit(kakaoResult)
		}
	}.flowOn(Dispatchers.IO).catch { emit(DataResourceResult.Error(it)) }
	
	override suspend fun logout(): Flow<DataResourceResult<Unit>> = flow {
		emit(DataResourceResult.Loading)
		emit(kakaoDataSource.logout())
	}.flowOn(Dispatchers.IO).catch { emit(DataResourceResult.Error(it)) }
	
	override suspend fun isLoggedIn(): Flow<DataResourceResult<Boolean>> = flow {
		emit(DataResourceResult.Loading)
		val result = kakaoDataSource.isLoggedIn()
		emit(DataResourceResult.Success(result))
	}.flowOn(Dispatchers.IO).catch { emit(DataResourceResult.Error(it)) }
}