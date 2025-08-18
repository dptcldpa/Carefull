package com.cases.carefull.data.datasource

import android.content.Context
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.util.DataResourceResult
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoDataSourceImpl: KaKaoDataSource {
    override suspend fun login(context: Context): DataResourceResult<UserInfo> = runCatching {
        // 카카오톡 로그인 + 사용자 정보 가져오기
        suspendCancellableCoroutine<UserInfo> { continuation ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (token != null) {
                    // 로그인 성공 시 사용자 정보 가져오기
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

    override suspend fun isLoggedIn(): Boolean = try {
        suspendCancellableCoroutine { continuation: CancellableContinuation<Boolean> ->
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    continuation.resume(false)
                } else {
                    continuation.resume(user != null)
                }
            }
        }
    } catch (e: Exception) {
        false
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
                }
            }
        }
    }.fold(
        onSuccess = { DataResourceResult.Success(it) },
        onFailure = { DataResourceResult.Error(it) }
    )
}

//// 로그인 처리
//override suspend fun login(context: Context): DataResourceResult<UserInfo> = runCatching {
//    val token = suspendCancellableCoroutine<OAuthToken> { cont ->
//        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
//            if (error != null) cont.resumeWithException(error)
//            else if (token != null) cont.resume(token)
//        }
//    }
//
//    val user = suspendCancellableCoroutine<User> { cont ->
//        UserApiClient.instance.me { kakaoUser, error ->
//            if (error != null) cont.resumeWithException(error)
//            else if (kakaoUser != null) {
//                val info = User(
//                    loginId = kakaoUser.id.toString(),
//                    nickname = kakaoUser.kakaoAccount?.profile?.nickname.orEmpty(),
//                    profileImage = kakaoUser.kakaoAccount?.profile?.profileImageUrl.orEmpty()
//                )
//                cont.resume(info)
//            }
//        }
//    }
//
//    user
//}.fold(
//    onSuccess = { DataResourceResult.Success(it) },
//    onFailure = { DataResourceResult.Error(it) }
//)
//
//// 로그아웃 처리
//override suspend fun logout(): DataResourceResult<Unit> = runCancellable {
//    suspendCancellableCoroutine<Unit> { cont ->
//        UserApiClient.instance.logout { error ->
//            if (error != null) cont.resumeWithException(error)
//            else cont.resume(Unit)
//        }
//    }
//}.fold(
//    onSuccess = { DataResourceResult.Success(Unit) },
//    onFailure = { DataResourceResult.Error(it) }
//)
//
//// 앱 시작 시 자동 로그인 체크
//override suspend fun isLoggedIn(): Boolean = try {
//    UserApiClient.instance.tokenInfo() != null
//} catch (e: Exception) {
//    false
//}
//
//// 이미 로그인된 사용자 정보 가져오기
//override suspend fun getCurrentUser(): DataResourceResult<User> = runCatching {
//    suspendCancellableCoroutine<User> { cont ->
//        UserApiClient.instance.me { kakaoUser, error ->
//            if (error != null) cont.resumeWithException(error)
//            else if (kakaoUser != null) {
//                val info = User(
//                    loginId = kakaoUser.id.toString(),
//                    nickname = kakaoUser.kakaoAccount?.profile?.nickname.orEmpty(),
//                    profileImage = kakaoUser.kakaoAccount?.profile?.profileImageUrl.orEmpty()
//                )
//                cont.resume(info)
//            }
//        }
//    }
//}.fold(
//    onSuccess = { DataResourceResult.Success(it) },
//    onFailure = { DataResourceResult.Error(it) }
//)
