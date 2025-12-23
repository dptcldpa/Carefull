package com.cases.carefull.features.carefullmainui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.UserInfo
import com.cases.carefull.domain.repository.account.KakaoRepository
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OAuthViewModel @Inject constructor(
	private val kakaoRepository: KakaoRepository,
) : ViewModel() {

	private val _uiState = MutableStateFlow(UserUiState())
	val uiState = _uiState.asStateFlow()

	private var currentUser: UserInfo? = null

	fun loginWithKakao() {
		viewModelScope.launch(Dispatchers.IO) {
			kakaoRepository.login().collectLatest { result ->
				withContext(Dispatchers.Main) {
					_uiState.value = when (result) {
						is DataResourceResult.Success -> {
							currentUser = result.data
							UserUiState(userInfo = result.data)
						}
						is DataResourceResult.Error -> UserUiState(
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

	fun logoutWithKakao() {
		viewModelScope.launch(Dispatchers.IO) {
			kakaoRepository.logout().collectLatest { result ->
				withContext(Dispatchers.Main) {
					_uiState.value = when (result) {
						is DataResourceResult.Success -> {
							currentUser = null
							UserUiState()
						} // 모든 상태 초기화
						is DataResourceResult.Error -> UserUiState(
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

	fun checkLoggedInState() {
		viewModelScope.launch(Dispatchers.IO) {
			kakaoRepository.isLoggedIn().collectLatest { result ->
				withContext(Dispatchers.Main) {
					_uiState.value = when (result) {
						is DataResourceResult.Success -> {
							if (result.data) {
								loadCurrentUserAfterKakaoLogin()
								_uiState.value.copy(isLoading = true)
							} else {
								// 로그인 상태가 아니라면 로딩 종료
//                            _uiState.value = UserUiState(isLoading = false)
								UserUiState(isLoading = false, userInfo = null)
							}
						}

						is DataResourceResult.Error -> {
							UserUiState(errorMessage = result.exception.message)
						}
						is DataResourceResult.Loading -> {
							UserUiState(isLoading = true)
						}
					}
				}
			}
		}
	}

	fun loadCurrentUserAfterKakaoLogin() {
		viewModelScope.launch(Dispatchers.IO) {
			kakaoRepository.getCurrentUser().collectLatest { result ->
				_uiState.value = when (result) {
					is DataResourceResult.Success -> {
						currentUser = result.data
						UserUiState(userInfo = result.data)
					}
					is DataResourceResult.Error -> UserUiState(
						errorMessage = result.exception.message
					)
					is DataResourceResult.Loading -> _uiState.value.copy(
						isLoading = true,
						errorMessage = null)

				}
			}
		}
	}

	fun errorMessageShown() {
		_uiState.value = _uiState.value.copy(errorMessage = null)
	}
}
