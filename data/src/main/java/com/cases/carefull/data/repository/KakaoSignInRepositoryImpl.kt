package com.cases.carefull.data.repository

import android.content.Context
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.repository.KakaoSignInRepository
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoSignInRepositoryImpl(
    private val context: Context
) : KakaoSignInRepository {

    override suspend fun kakaoLogin(): UserInfo {
        val token = loginWithKakao()
        return getUserInfo()
    }

    private suspend fun loginWithKakao(): OAuthToken = // (콜백으로)
        suspendCancellableCoroutine { continuation ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (token != null) {
                    continuation.resume(token)
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }

    private suspend fun getUserInfo(): UserInfo =
        suspendCancellableCoroutine { continuation ->
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
                }
            }
        }
}