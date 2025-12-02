package com.cases.carefull.data.repository.account

import android.content.Context
import com.cases.carefull.data.datasource.UserDataSource
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.repository.account.UserRepository
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
	private val userDataSource: UserDataSource
) : UserRepository {
	override fun getUser(userId: String): Flow<DataResourceResult<UserInfo?>> = flow {
		emit(DataResourceResult.Loading)

		val user = userDataSource.readUser(userId)
		emit(DataResourceResult.Success(user))
	}
	.catch { emit(DataResourceResult.Error(it)) }
	.flowOn(Dispatchers.IO)

	override fun updateUser(user: UserInfo): Flow<DataResourceResult<Unit>> = flow {
		emit(DataResourceResult.Loading)

		userDataSource.updateUser(user)
		emit(DataResourceResult.Success(Unit))
	}
	.catch { emit(DataResourceResult.Error(it)) }
	.flowOn(Dispatchers.IO)
}