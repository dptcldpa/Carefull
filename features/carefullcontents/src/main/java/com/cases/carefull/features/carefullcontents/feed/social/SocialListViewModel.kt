package com.cases.carefull.features.carefullcontents.feed.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.feed.SocialCategory
import com.cases.carefull.domain.repository.feed.SocialCommentRepository
import com.cases.carefull.domain.repository.feed.SocialPostRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.domain.util.FeedConfig
import com.cases.carefull.features.carefullcontents.util.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialListViewModel @Inject constructor(
    private val socialPostRepository: SocialPostRepository,
    private val socialCommentRepository: SocialCommentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialListUiState())
    val uiState: StateFlow<SocialListUiState> = _uiState.asStateFlow()

    init {
        loadPosts(isRefresh = true)
    }

    fun loadPosts(isRefresh: Boolean, category: SocialCategory? = null) {
        val currentCategory = category ?: uiState.value.selectedCategory
        if (uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRefreshing = isRefresh,
                    isLoading = true,
                    selectedCategory = currentCategory,
                    error = null
                )
            }

            val lastPost = if (isRefresh) null else uiState.value.posts.lastOrNull()

            val result = socialPostRepository.getPosts(currentCategory, lastPost)

            when (result) {
                is DataResourceResult.Success -> {
                    val newPosts = result.data
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            posts = if (isRefresh) newPosts else state.posts + newPosts,
                            isEndReached = newPosts.size < FeedConfig.POST_PAGING_SIZE,
                            error = null
                        )
                    }
                }

                is DataResourceResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.exception.asUiText()
                    )
                }

                else -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }
}
