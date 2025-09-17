package com.cases.carefull.features.carefullcontents.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cases.carefull.domain.model.Comment
import com.cases.carefull.domain.model.Post
import com.cases.carefull.features.carefullcommon.components.CustomTopAppBar
import com.cases.carefull.features.carefullcommon.navigation.FeedRoute
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
	navController: NavController,
	viewModel: PostDetailViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	var commentInput by remember { mutableStateOf("") }
	var showDeleteDialog by remember { mutableStateOf(false) }
	
	var showCommentDeleteDialog by remember { mutableStateOf(false) }
	var commentToDelete by remember { mutableStateOf<Comment?>(null) }
	
	var showCommentEditDialog by remember { mutableStateOf(false) }
	var commentToEdit by remember { mutableStateOf<Comment?>(null) }
	
	val lifecycleOwner = LocalLifecycleOwner.current
	DisposableEffect(lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) {
				viewModel.fetchPostDetail()
				viewModel.fetchComments()
			}
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer)
		}
	}
	LaunchedEffect(uiState.isDeleted) {
		if (uiState.isDeleted) {
			navController.popBackStack()
		}
	}
	
	if (showCommentEditDialog && commentToEdit != null) {
		CommentEditDialog(
			initialContent = commentToEdit!!.content,
			onConfirm = { newContent ->
				viewModel.updateComment(commentToEdit!!.id, newContent)
				showCommentEditDialog = false
				commentToEdit = null
			},
			onDismiss = {
				showCommentEditDialog = false
				commentToEdit = null
			}
		)
	}
	
	if (showCommentDeleteDialog && commentToDelete != null) {
		DeleteConfirmationDialog(
			title = "댓글 삭제 확인",
			text = "이 댓글을 정말로 삭제하시겠습니까?",
			onConfirm = {
				viewModel.deleteComment(commentToDelete!!.id)
				showCommentDeleteDialog = false
				commentToDelete = null
			},
			onDismiss = {
				showCommentDeleteDialog = false
				commentToDelete = null
			}
		)
	}
	
	if (showDeleteDialog) {
		DeleteConfirmationDialog(
			title = "게시글 삭제 확인",
			text = "이 게시글을 정말로 삭제하시겠습니까?",
			onConfirm = {
				showDeleteDialog = false
				viewModel.deletePost()
			},
			onDismiss = {
				showDeleteDialog = false
			}
		)
	}
	Column(modifier = Modifier.fillMaxSize()) {
		CustomTopAppBar(
			title = "게시글",
			navigationIcon = {
				IconButton(onClick = { navController.popBackStack() }) {
					Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
				}
			},
			actions = {
				if (uiState.post?.userId == uiState.currentUserId) {
					IconButton(onClick = {
						navController.navigate(FeedRoute.CreatePostScreen(postId = uiState.post!!.id))
					}) {
						Icon(Icons.Default.Edit, contentDescription = "게시글 수정")
					}
					IconButton(onClick = { showDeleteDialog = true }) {
						Icon(Icons.Default.Delete, contentDescription = "게시글 삭제")
					}
				}
			}
		)
		Box(
			modifier = Modifier
				.weight(1f)
		) {
			if (uiState.isLoading && uiState.post == null) {
				CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
			} else if (uiState.error != null) {
				Text(
					text = "에러 발생: ${uiState.error}",
					modifier = Modifier.align(Alignment.Center)
				)
			} else if (uiState.post != null) {
				PostDetailContent(
					post = uiState.post!!,
					comments = uiState.comments,
					userLikedPost = uiState.userLikedPost,
					currentUserId = uiState.currentUserId,
					onToggleLike = viewModel::toggleLike,
					onDeleteCommentClick = { comment ->
						commentToDelete = comment
						showCommentDeleteDialog = true
					},
					onEditCommentClick = { comment ->
						commentToEdit = comment
						showCommentEditDialog = true
					}
				)
			}
		}
		CommentInputSection(
			commentInput = commentInput,
			onCommentInputChanged = { commentInput = it },
			onSendComment = {
				viewModel.addComment(commentInput)
				commentInput = ""
			},
			isLoading = uiState.isLoading
		)
	}
}

@Composable
fun DeleteConfirmationDialog(
	title: String,
	text: String,
	onConfirm: () -> Unit,
	onDismiss: () -> Unit
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(title) },
		text = { Text(text) },
		confirmButton = { TextButton(onClick = onConfirm) { Text("삭제") } },
		dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
	)
}

@Composable
fun PostDetailContent(
	post: Post,
	comments: List<Comment>,
	userLikedPost: Boolean,
	onToggleLike: () -> Unit,
	currentUserId: String?,
	onDeleteCommentClick: (Comment) -> Unit,
	onEditCommentClick: (Comment) -> Unit
) {
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp)
	) {
		item {
			Column {
				post.imageUrl?.let {
					AsyncImage(
						model = it,
						contentDescription = post.title,
						modifier = Modifier
							.fillMaxWidth()
							.height(250.dp),
						contentScale = ContentScale.Crop
					)
					Spacer(modifier = Modifier.height(16.dp))
				}
				Text(text = post.title, style = MaterialTheme.typography.headlineSmall)
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = "${post.userId} | ${post.category}",
					style = MaterialTheme.typography.bodyMedium
				)
				Spacer(modifier = Modifier.height(8.dp))
				Text(text = post.content, style = MaterialTheme.typography.bodyLarge)
				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						IconButton(onClick = onToggleLike) {
							Icon(
								imageVector = if (userLikedPost) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
								contentDescription = "좋아요",
								tint = if (userLikedPost) Color.Red else MaterialTheme.colorScheme.onSurface
							)
						}
						Text(
							text = post.likeCount.toString(),
							style = MaterialTheme.typography.bodyLarge
						)
						Spacer(modifier = Modifier.width(16.dp))
						Icon(
							Icons.Filled.Comment,
							contentDescription = "댓글",
							tint = MaterialTheme.colorScheme.onSurface
						)
						Spacer(modifier = Modifier.width(4.dp))
						Text(
							text = post.commentCount.toString(),
							style = MaterialTheme.typography.bodyLarge
						)
					}
					Text(
						text = SimpleDateFormat(
							"yyyy.MM.dd HH:mm",
							Locale.getDefault()
						).format(post.createdAt),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				HorizontalDivider(
					modifier = Modifier.padding(vertical = 4.dp),
					thickness = DividerDefaults.Thickness,
					color = DividerDefaults.color
				)
				Text("댓글 (${comments.size})", style = MaterialTheme.typography.labelLarge)
				Spacer(modifier = Modifier.height(8.dp))
			}
		}
		
		// 댓글 목록
		items(comments) { comment ->
			CommentItem(
				comment = comment,
				isOwnComment = comment.userId == currentUserId,
				onDeleteClick = { onDeleteCommentClick(comment) },
				onEditClick = { onEditCommentClick(comment) }
			)
			Spacer(modifier = Modifier.height(8.dp))
		}
	}
}

@Composable
fun CommentItem(
	comment: Comment,
	isOwnComment: Boolean,
	onDeleteClick: () -> Unit,
	onEditClick: () -> Unit
) {
	
	var isMenuExpanded by remember { mutableStateOf(false) }
	
	Card(
		modifier = Modifier.fillMaxWidth(),
		border = BorderStroke(1.dp, Color.LightGray),
		colors = CardDefaults.cardColors(
			containerColor = Color.White,
			contentColor = Color.Black
		)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(12.dp)
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = "CareFull",
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.weight(1f, fill = false),
						maxLines = 1
					)
					Text(
						text = SimpleDateFormat(
							"MM.dd HH:mm",
							Locale.getDefault()
						).format(comment.createdAt),
						style = MaterialTheme.typography.bodySmall
					)
				}
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = comment.content,
					style = MaterialTheme.typography.titleSmall
				)
			}
			
			if (isOwnComment) {
				Box {
					IconButton(onClick = { isMenuExpanded = true }) {
						Icon(Icons.Default.MoreVert, contentDescription = "댓글 메뉴")
					}
					DropdownMenu(
						expanded = isMenuExpanded,
						onDismissRequest = { isMenuExpanded = false }
					) {
						DropdownMenuItem(
							text = { Text("수정") },
							onClick = {
								isMenuExpanded = false
								onEditClick()
							}
						)
						DropdownMenuItem(
							text = { Text("삭제") },
							onClick = {
								isMenuExpanded = false
								onDeleteClick()
							}
						)
					}
				}
			}
		}
	}
}

@Composable
fun CommentEditDialog(
	initialContent: String,
	onConfirm: (String) -> Unit,
	onDismiss: () -> Unit
) {
	var text by remember { mutableStateOf(initialContent) }
	
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("댓글 수정") },
		text = {
			OutlinedTextField(
				value = text,
				onValueChange = { text = it },
				modifier = Modifier.fillMaxWidth(),
				label = { Text("내용") }
			)
		},
		confirmButton = {
			TextButton(
				onClick = { onConfirm(text) },
				enabled = text.isNotBlank()
			) {
				Text("수정")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text("취소")
			}
		}
	)
}

@Composable
fun CommentInputSection(
	commentInput: String,
	onCommentInputChanged: (String) -> Unit,
	onSendComment: () -> Unit,
	isLoading: Boolean
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		OutlinedTextField(
			value = commentInput,
			onValueChange = onCommentInputChanged,
			label = { Text("댓글 입력") },
			modifier = Modifier.weight(1f),
			maxLines = 3,
//			keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
//			keyboardActions = KeyboardActions(onSend = { onSendComment() }),
			shape = RoundedCornerShape(16.dp)
		)
		Spacer(modifier = Modifier.width(8.dp))
		IconButton(
			onClick = onSendComment,
			enabled = commentInput.isNotBlank() && !isLoading
		) {
			Icon(
				Icons.AutoMirrored.Filled.Send,
				tint = if (commentInput.isNotBlank() && !isLoading)
					MaterialTheme.colorScheme.primary
				else
					Color.Gray,
				contentDescription = "댓글 전송"
			)
		}
	}
}