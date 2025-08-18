package com.cases.carefull.features.carefullmainui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cases.carefull.domain.repository.KakaoSignInRepository
import com.cases.carefull.domain.repository.UserRepository

class OAuthViewModelFactory(
    private val kakaoSignInRepository: KakaoSignInRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OAuthViewModel(kakaoSignInRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

//class KakaoLoginViewModelFactory(
//    private val repository: KakaoSignInRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(KakaoLoginViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return KakaoLoginViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}