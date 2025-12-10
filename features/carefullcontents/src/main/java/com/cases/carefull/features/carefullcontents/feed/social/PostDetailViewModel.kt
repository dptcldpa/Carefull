package com.cases.carefull.features.carefullcontents.feed.social

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.repository.feed.SocialCommentRepository
import com.cases.carefull.domain.repository.feed.SocialPostRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.features.carefullcontents.util.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val socialPostRepository: SocialPostRepository,
    private val socialCommentRepository: SocialCommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: String = savedStateHandle.get<String>("postId")
        ?: throw IllegalStateException("아이디 정보 필요")

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    private var lastFetchedTimeMillis: Long = 0
    private val CACHE_DURATION_MS = 1 * 60 * 1000L // 1분

    init {
        _uiState.update { it.copy(currentUserId = "test") }
        fetchData()
    }

    private fun fetchData() {
        fetchPostDetail()
        fetchComments()
        checkLikeStatus()
        lastFetchedTimeMillis = System.currentTimeMillis()
    }

    fun refreshDataIfNeeded() {
        if (System.currentTimeMillis() - lastFetchedTimeMillis > CACHE_DURATION_MS) {
            fetchData()
        }
    }

    fun fetchPostDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = socialPostRepository.getPostDetail(postId)
            when (result) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, post = result.data) }
                }

                is DataResourceResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.asUiText()
                        )
                    }
                }

                else -> {}
            }
        }
    }

    fun fetchComments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = socialCommentRepository.getComments(postId)
            when (result) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(comments = result.data) }
                }

                is DataResourceResult.Error -> {
                    _uiState.update {
                        it.copy(error = result.exception.asUiText())
                    }
                }

                else -> {}
            }
        }
    }

    fun addComment(content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = socialCommentRepository.submitComment(postId, null, content)
            when (result) {
                is DataResourceResult.Success -> {
                    fetchComments()
                    _uiState.update { currentState ->
                        val updatedPost = currentState.post?.copy(
                            commentCount = currentState.post.commentCount + 1
                        )
                        currentState.copy(isLoading = false, post = updatedPost)
                    }
                }

                is DataResourceResult.Error -> {
                    _uiState.update {
                        it.copy(error = result.exception.asUiText())
                    }
                }

                else -> {}
            }
        }
    }

    fun updateComment(commentId: String, newContent: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = socialCommentRepository.submitComment(postId, commentId, newContent)
            when (result) {
                is DataResourceResult.Success -> {
                    fetchComments()
                }

                is DataResourceResult.Error -> {
                    _uiState.update {
                        it.copy(error = result.exception.asUiText())
                    }
                }

                else -> {}
            }
        }
    }

    fun toggleLike() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = socialPostRepository.toggleLike(postId)) {
                is DataResourceResult.Success -> {
                    _uiState.update { currentState ->
                        val currentLiked = currentState.userLikedPost
                        val newLikeCount = if (currentLiked) {
                            currentState.post?.likeCount?.minus(1) ?: 0
                        } else {
                            currentState.post?.likeCount?.plus(1) ?: 1
                        }
                        val updatedPost = currentState.post?.copy(likeCount = newLikeCount)

                        currentState.copy(
                            userLikedPost = !currentLiked,
                            post = updatedPost
                        )
                    }
                }

                is DataResourceResult.Error -> {
                    _uiState.update {
                        it.copy(error = result.exception.asUiText())
                    }
                }

                else -> {}
            }
        }
    }

    private fun checkLikeStatus() {
        viewModelScope.launch {
            when (val result = socialPostRepository.hasUserLikedPost(postId)) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(userLikedPost = result.data) }
                }

                is DataResourceResult.Error -> {
                    _uiState.update {
                        it.copy(error = result.exception.asUiText())
                    }
                }
                    else -> {}
                }
            }
        }

        fun deleteComment(commentId: String) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                when (val result = socialCommentRepository.deleteComment(postId, commentId)) {
                    is DataResourceResult.Success -> {
                        fetchComments()

                        _uiState.update { currentState ->
                            val updatedPost = currentState.post?.copy(
                                commentCount = currentState.post.commentCount - 1
                            )
                            currentState.copy(post = updatedPost)
                        }
                    }

                    is DataResourceResult.Error -> {
                        _uiState.update {
                            it.copy(error = result.exception.asUiText())
                        }
                    }

                    else -> {}
                }
            }
        }

        fun deletePost() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                when (val result = socialPostRepository.deletePost(postId)) {
                    is DataResourceResult.Success -> {
                        _uiState.update { it.copy(isLoading = false, isDeleted = true) }
                    }

                    is DataResourceResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.exception.asUiText()
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
