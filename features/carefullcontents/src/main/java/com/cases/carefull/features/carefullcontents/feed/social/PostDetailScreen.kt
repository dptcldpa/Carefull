package com.cases.carefull.features.carefullcontents.feed.social

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cases.carefull.domain.model.feed.Comment
import com.cases.carefull.domain.model.feed.Post
import com.cases.carefull.features.carefullcommon.components.CommonAlertDialog
import com.cases.carefull.features.carefullcommon.components.TextOutLinedTextField
import com.cases.carefull.features.carefullcommon.components.CustomTopAppBar
import com.cases.carefull.features.carefullcommon.navigation.FeedRoute
import java.text.SimpleDateFormat
import java.util.Locale
import com.cases.carefull.features.carefullcommon.R

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
                viewModel.refreshDataIfNeeded()
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
            title = stringResource(R.string.dialog_title_confirm_delete_comment),
            text = stringResource(R.string.dialog_message_confirm_delete_comment),
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
            title = stringResource(R.string.dialog_title_confirm_delete_post),
            text = stringResource(R.string.dialog_message_confirm_delete_post),
            onConfirm = {
                showDeleteDialog = false
                viewModel.deletePost()
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.common_post),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                },
                actions = {
                    if (uiState.post?.userId == uiState.currentUserId) {
                        IconButton(onClick = {
                            navController.navigate(FeedRoute.CreatePostScreen(postId = uiState.post!!.id))
                        }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.post_button_edit)
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.post_button_delete)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            val focusManager = LocalFocusManager.current
            CommentInputSection(
                modifier = Modifier
                    .navigationBarsPadding(),

                commentInput = commentInput,
                onCommentInputChanged = { commentInput = it },
                onSendComment = {
                    viewModel.addComment(commentInput)
                    commentInput = ""
                    focusManager.clearFocus() // 전송 후 키보드 숨기기
                },
                isLoading = uiState.isLoading
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading && uiState.post == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.error_generic_format, uiState.error!!),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
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
    }
}

@Composable
fun DeleteConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    CommonAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        content = { Text(text) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.common_delete)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) } }
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
                    text = stringResource(
                        R.string.post_info_format_user_category,
                        post.userId,
                        post.category
                    ),
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
                                contentDescription = stringResource(R.string.common_like),
                                tint = if (userLikedPost) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = post.likeCount.toString(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.Comment,
                            contentDescription = stringResource(R.string.common_comment),
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
                            stringResource(R.string.date_format_full),
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
                Text(
                    stringResource(R.string.comment_count_format, comments.size),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 댓글 목록
        items(comments, key = { it.id }) { comment ->
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
                            stringResource(R.string.date_format_short),
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
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.comment_menu)
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.common_edit)) },
                            onClick = {
                                isMenuExpanded = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.common_delete)) },
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

    CommonAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.comment_edit_title)) },
        content = {
            TextOutLinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.post_label_content)) }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text(stringResource(R.string.common_edit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}

@Composable
fun CommentInputSection(
    modifier: Modifier = Modifier,
    commentInput: String,
    onCommentInputChanged: (String) -> Unit,
    onSendComment: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentInput,
                onValueChange = onCommentInputChanged,
                label = { Text(stringResource(R.string.comment_input_hint)) },
                modifier = Modifier.weight(1f),
                maxLines = 3,
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
                    contentDescription = stringResource(R.string.comment_send_button)
                )
            }
        }
    }
}
