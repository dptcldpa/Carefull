package com.cases.carefull.features.carefullcontents.feed

import android.util.Log
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
class SocialListViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialListUiState())
    val uiState: StateFlow<SocialListUiState> = _uiState.asStateFlow()

    init {
        fetchPosts(SocialCategory.ALL)
    }

    fun fetchPosts(category: SocialCategory, isLoading: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = !isLoading,
                    error = null,
                    selectedCategory = category
                )
            }

            when (val result = socialRepository.getPosts(category)) {
                is BaseResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            posts = result.data
                        )
                    }
                }

                is BaseResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.exception.message ?: "게시글 로드 중 오류 발생"
                        )

                    }
                }
            }
        }
    }
}