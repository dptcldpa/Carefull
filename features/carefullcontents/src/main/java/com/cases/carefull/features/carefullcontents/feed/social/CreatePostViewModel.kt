package com.cases.carefull.features.carefullcontents.feed.social

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.feed.FeedException
import com.cases.carefull.domain.model.feed.SocialCategory
import com.cases.carefull.domain.repository.feed.SocialPostRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcontents.util.UiText
import com.cases.carefull.features.carefullcontents.util.UiText.StringResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val socialPostRepository: SocialPostRepository,
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
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = socialPostRepository.getPostDetail(postId!!)) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, initialPost = result.data) }
                }

                is DataResourceResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = convertToUiText(result.exception)
                        )
                    }
                }

                else -> {}
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

            val result =
                socialPostRepository.submitPost(
                    postId = this@CreatePostViewModel.postId,
                    title = title,
                    content = content,
                    category = category,
                    imageUriString = imageUri?.toString()
                )

            when (result) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }

                is DataResourceResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = convertToUiText(result.exception)
                        )
                    }
                }

                else -> {}
            }
        }
    }

    fun resetState() {
        _uiState.update { CreatePostUiState() }
    }

    private fun convertToUiText(e: Throwable): UiText {
        return when (e) {
            is FeedException.NotFound -> StringResource(R.string.error_post_load_failed)
            is FeedException.Unauthorized -> StringResource(R.string.error_no_permission)
            is FeedException.NetworkError -> StringResource(R.string.error_fetch_data_failed)
            else -> {
                UiText.StringResource(R.string.error_unknown)
            }
        }
    }
}
