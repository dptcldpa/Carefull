package com.cases.carefull.features.carefullmainui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OAuthViewModel @Inject constructor(
	private val userRepository: UserRepository
) : ViewModel() {
	
	private val _uiState = MutableStateFlow(UserUiState())
	val uiState = _uiState.asStateFlow()
	
	init {
		checkAuthenticationState()
	}
	
	fun loginWithKakao() {
		viewModelScope.launch {
			userRepository.login().collectLatest { result ->
				_uiState.update { currentState ->
					when (result) {
						is DataResourceResult.Success -> {
							currentState.copy(
								isLoading = false,
								userInfo = result.data,
								errorMessage = null
							)
						}
						
						is DataResourceResult.Error -> {
							currentState.copy(
								isLoading = false,
								errorMessage = result.exception.message
							)
						}
						
						is DataResourceResult.Loading -> {
							currentState.copy(
								isLoading = true,
								errorMessage = null
							)
						}
					}
				}
			}
		}
	}
	
	fun logoutWithKakao() {
		viewModelScope.launch {
			userRepository.logout().collectLatest { result ->
				_uiState.update { currentState ->
					when (result) {
						is DataResourceResult.Success -> {
							currentState.copy(
								isLoading = false,
								userInfo = null,
								errorMessage = null
							)
						}
						
						is DataResourceResult.Error -> {
							currentState.copy(
								isLoading = false,
								errorMessage = result.exception.message
							)
						}
						
						is DataResourceResult.Loading -> {
							currentState.copy(
								isLoading = true,
								errorMessage = null
							)
						}
					}
				}
			}
		}
	}
	
	fun checkAuthenticationState() {
		viewModelScope.launch {
			userRepository.getCurrentUser().collectLatest { result ->
				_uiState.update { currentState ->
					when (result) {
						is DataResourceResult.Success -> {
							currentState.copy(
								isAuthenticating = false,
								userInfo = result.data,
							)
						}
						
						is DataResourceResult.Error -> {
							currentState.copy(
								isAuthenticating = false,
								userInfo = null,
							)
						}
						
						is DataResourceResult.Loading -> {
							currentState.copy(
								isAuthenticating = true
							)
							
						}
					}
				}
			}
		}
	}
	
	fun errorMessageShown() {
		_uiState.update {
			it.copy(
				errorMessage = null
			)
		}
	}
}