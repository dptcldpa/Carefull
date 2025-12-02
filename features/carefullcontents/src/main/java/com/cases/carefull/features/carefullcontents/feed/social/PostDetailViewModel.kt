package com.cases.carefull.features.carefullcontents.feed.social

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.repository.feed.SocialRepository
import com.cases.carefull.domain.util.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
	private val socialRepository: SocialRepository,
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
			when (val result = socialRepository.getPostDetail(postId)) {
				is BaseResult.Success -> {
					_uiState.update { it.copy(isLoading = false, post = result.data) }
				}
				is BaseResult.Error -> {
					_uiState.update { it.copy(isLoading = false, error = result.exception.message ?: "게시글 상세 로드 실패") }
				}
			}
		}
	}
	
	fun fetchComments() {
		viewModelScope.launch {
			_uiState.update { it.copy(error = null) }
			when (val result = socialRepository.getComments(postId)) {
				is BaseResult.Success -> {
					_uiState.update { it.copy(comments = result.data) }
				}
				is BaseResult.Error -> {
					_uiState.update { it.copy(error = result.exception.message ?: "댓글 로드 실패") }
				}
			}
		}
	}
	
	fun addComment(content: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, error = null) }
			when (val result = socialRepository.addComment(postId, content)) {
				is BaseResult.Success -> {
					fetchComments()
					_uiState.update { currentState ->
						val updatedPost = currentState.post?.copy(
							commentCount = currentState.post.commentCount + 1
						)
						currentState.copy(isLoading = false, post = updatedPost)
					}
				}
				is BaseResult.Error -> {
					_uiState.update { it.copy(isLoading = false, error = result.exception.message ?: "댓글 작성 실패") }
				}
			}
		}
	}
	
	fun updateComment(commentId: String, newContent: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(error = null) }
			val result = socialRepository.updateComment(postId, commentId, newContent)
			when (result) {
				is BaseResult.Success -> {
					fetchComments()
				}
				is BaseResult.Error -> {
					_uiState.update {
						it.copy(error = result.exception.message ?: "댓글 수정 실패")
					}
				}
			}
		}
	}
	fun toggleLike() {
		viewModelScope.launch {
			_uiState.update { it.copy(error = null) }
			when (val result = socialRepository.toggleLike(postId)) {
				is BaseResult.Success -> {
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
				is BaseResult.Error -> {
					_uiState.update { it.copy(error = result.exception.message ?: "좋아요 처리 실패") }
				}
			}
		}
	}
	private fun checkLikeStatus() {
		viewModelScope.launch {
			when (val result = socialRepository.hasUserLikedPost(postId)) {
				is BaseResult.Success -> {
					_uiState.update { it.copy(userLikedPost = result.data) }
				}
				is BaseResult.Error -> {
					_uiState.update { it.copy(error = result.exception.message ?: "좋아요 상태 확인 실패") }
				}
			}
		}
	}
	fun deleteComment(commentId: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(error = null) }
			when (val result = socialRepository.deleteComment(postId, commentId)) {
				is BaseResult.Success -> {
					fetchComments()
					
					_uiState.update { currentState ->
						val updatedPost = currentState.post?.copy(
							commentCount = currentState.post.commentCount - 1
						)
						currentState.copy(post = updatedPost)
					}
				}
				is BaseResult.Error -> {
					_uiState.update {
						it.copy(error = result.exception.message ?: "댓글 삭제 실패")
					}
				}
			}
		}
	}
	fun deletePost() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, error = null) }
			when (val result = socialRepository.deletePost(postId)) {
				is BaseResult.Success -> {
					_uiState.update { it.copy(isLoading = false, isDeleted = true) }
				}
				is BaseResult.Error -> {
					_uiState.update {
						it.copy(
							isLoading = false,
							error = result.exception.message ?: "게시글 삭제 실패"
						)
					}
				}
			}
		}
	}
}