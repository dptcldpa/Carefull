package com.cases.carefull.features.carefullcontents.feed.social

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.SocialCategory
import com.cases.carefull.domain.repository.SocialRepository
import com.cases.carefull.domain.util.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
	private val socialRepository: SocialRepository,
	savedStateHandle: SavedStateHandle
) : ViewModel() {
	
	private val postId: String? = savedStateHandle.get<String>("postId")
	private val isEditMode = postId != null
	private val _uiState = MutableStateFlow(CreatePostUiState())
	val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()
	
	init {
		if (isEditMode) {
			loadInitialPost()
		}
	}
	
	private fun loadInitialPost() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			when (val result = socialRepository.getPostDetail(postId!!)) {
				is BaseResult.Success -> {
					_uiState.update { it.copy(isLoading = false, initialPost = result.data) }
				}
				is BaseResult.Error -> {
					_uiState.update { it.copy(isLoading = false, error = result.exception.message) }
				}
			}
		}
	}
	fun submitPost(
		title: String,
		content: String,
		category: SocialCategory,
		imageUri: Uri?
	) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }
			
			val result = if (isEditMode) {
				socialRepository.updatePost(
					postId = postId!!,
					title = title,
					content = content,
					category = category,
					imageUriString = imageUri?.toString()
				)
			} else {
				socialRepository.createPost(title, content, category, imageUri?.toString())
			}
			
			when (result) {
				is BaseResult.Success -> {
					_uiState.update { it.copy(isLoading = false, isSuccess = true) }
				}
				is BaseResult.Error -> {
					_uiState.update {
						it.copy(
							isLoading = false,
							error = result.exception.message ?: if(isEditMode) "수정 실패" else "작성 실패"
						)
					}
				}
			}
		}
	}
	fun resetState() {
		_uiState.update { CreatePostUiState() }
	}
}