package com.cases.carefull.features.carefullmainui.screen.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.data.repository.UserRepositoryImpl
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.repository.KakaoSignInRepository
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OAuthViewModel(
//    private val kakaoSignInRepository: KakaoSignInRepository,
//    private val userRepository: UserRepository
    private val userRepositoryImpl: UserRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    fun login(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            userRepositoryImpl.loginWithKakao(context).collectLatest { result ->
                _uiState.value = when (result) {
                    is DataResourceResult.Success -> _uiState.value.copy(
                        userInfo = result.data,
                        isLoading = false,
                        errorMessage = null
                    )
                    is DataResourceResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                    is DataResourceResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            userRepositoryImpl.logout().collectLatest { result ->
                _uiState.value = when (result) {
                    is DataResourceResult.Success -> _uiState.value.copy(
                        userInfo = null,
                        isLoading = false,
                        errorMessage = null
                    )
                    is DataResourceResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                    is DataResourceResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            userRepositoryImpl.getCurrentUser().collectLatest { result ->
                _uiState.value = when (result) {
                    is DataResourceResult.Success -> _uiState.value.copy(
                        userInfo = result.data,
                        isLoading = false,
                        errorMessage = null
                    )
                    is DataResourceResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                    is DataResourceResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }
            }
        }
    }
}

//private val _uiState = MutableStateFlow(UserUiState())
//val uiState = _uiState.asStateFlow()
//
//fun handleKakaoLogin(context: Context) {
//    viewModelScope.launch {
//        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
//
//        try {
//            val userInfo = kakaoSignInRepository.kakaoLogin()
//            _uiState.update {
//                it.copy(
//                    isLoading = false,
//                    isLoggedIn = true,
//                    userInfo = userInfo
//                )
//                //
//            }
//            //repository 호출 - collect latest
//        } catch (e: Exception) {
//            _uiState.update {
//                it.copy(
//                    isLoading = false,
//                    errorMessage = e.message ?: "로그인 실패"
//                )
//            }
//        }
//    }
//}
//
//fun logout() {
//    _uiState.update {
//        it.copy(
//            isLoggedIn = false,
//            userInfo = null,
//            errorMessage = null
//        )
//    }
//}
//
//fun errorMessageShown() {
//    _uiState.update { it.copy(errorMessage = null) }
//}