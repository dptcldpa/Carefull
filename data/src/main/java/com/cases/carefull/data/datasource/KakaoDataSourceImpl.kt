package com.cases.carefull.data.datasource

import android.content.Context
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoDataSourceImpl @Inject constructor() : KaKaoDataSource {
    override suspend fun login(context: Context): DataResourceResult<UserInfo> = runCatching {
        suspendCancellableCoroutine<UserInfo> { continuation ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (token != null) {
                    UserApiClient.instance.me { user, userError ->
                        if (userError != null) {
                            continuation.resumeWithException(userError)
                        } else if (user != null) {
                            continuation.resume(
                                UserInfo(
                                    login_id = user.id?.toString() ?: "",
                                    nickname = user.kakaoAccount?.profile?.nickname ?: "",
                                    profile_image = user.kakaoAccount?.profile?.profileImageUrl ?: ""
                                )
                            )
                        }
                    }
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    }.fold(
        onSuccess = { DataResourceResult.Success(it) },
        onFailure = { DataResourceResult.Error(it) }
    )

    override suspend fun logout(): DataResourceResult<Unit> = runCatching {
        suspendCancellableCoroutine<Unit> { continuation ->
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else continuation.resume(Unit)
            }
        }
    }.fold(
        onSuccess = { DataResourceResult.Success(Unit) },
        onFailure = { DataResourceResult.Error(it) }
    )

    override suspend fun isLoggedIn(): Boolean {
        if (!AuthApiClient.instance.hasToken()) {
            return false
        }

        return try {
            val tokenInfo = suspendCancellableCoroutine<AccessTokenInfo?> { continuation ->
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        continuation.resume(null)
                    }
                    else {
                        continuation.resume(tokenInfo)
                    }
                }
            }
            tokenInfo != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getCurrentUser(): DataResourceResult<UserInfo> = runCatching {
        suspendCancellableCoroutine<UserInfo> { continuation ->
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (user != null) {
                    continuation.resume(
                        UserInfo(
                            login_id = user.id?.toString() ?: "",
                            nickname = user.kakaoAccount?.profile?.nickname ?: "",
                            profile_image = user.kakaoAccount?.profile?.profileImageUrl ?: ""
                        )
                    )
                } else {
                    continuation.resumeWithException(RuntimeException("Kakao user가 null"))
                }
            }
        }
    }.fold(
        onSuccess = { DataResourceResult.Success(it) },
        onFailure = { DataResourceResult.Error(it) }
    )
}