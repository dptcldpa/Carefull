package com.cases.carefull.features.carefullcontents.feed


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cases.carefull.domain.model.Post
import com.cases.carefull.features.carefullcommon.navigation.FeedRoute
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialListScreen(
	navController: NavController,
	viewModel: SocialListViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val categories = listOf("전체", "자유", "운동", "식단", "의료")
	val lifecycleOwner = LocalLifecycleOwner.current
	DisposableEffect(lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) {
				viewModel.fetchPosts(uiState.selectedCategory)
			}
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer)
		}
	}
	Box(modifier = Modifier.fillMaxSize()) {
		Column(modifier = Modifier.fillMaxSize()) {
			CategoryFilterChips(
				categories = categories,
				selectedCategory = uiState.selectedCategory,
				onCategorySelected = { category ->
					viewModel.fetchPosts(category)
				}
			)
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				if (uiState.isLoading) {
					CircularProgressIndicator()
				} else if (uiState.error != null) {
					Text(text = "에러 발생: ${uiState.error}")
				} else {
					PostList(
						posts = uiState.posts,
						onPostClick = { postId ->
							navController.navigate(FeedRoute.PostDetailScreen(postId = postId))
						}
					)
				}
			}
		}
		FloatingActionButton(
			onClick = {
				navController.navigate(FeedRoute.CreatePostScreen())
			},
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.padding(16.dp)
		) {
			Icon(Icons.Default.Create, contentDescription = "글쓰기")
		}
		
	}
}

@Composable
fun PostList(posts: List<Post>, onPostClick: (String) -> Unit) {
	if (posts.isEmpty()) {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text("게시글이 없습니다.")
		}
	} else {
		LazyColumn(
			modifier = Modifier.fillMaxSize(),
			contentPadding = PaddingValues(16.dp),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			items(posts, key = { it.id }) { post ->
				PostItem(post = post, onClick = { onPostClick(post.id) })
			}
		}
	}
}

@Composable
fun PostItem(post: Post, onClick: () -> Unit) {
	Card(
		onClick = onClick,
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 3.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
		colors = CardDefaults.cardColors(
			containerColor = Color.White
		)
	) {
		
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			post.imageUrl?.let {
				AsyncImage(
					model = it,
					contentDescription = post.title,
					modifier = Modifier
						.fillMaxWidth()
						.height(180.dp),
					contentScale = ContentScale.Crop
				)
				Spacer(modifier = Modifier.height(8.dp))
			}
			Text(text = post.title, style = MaterialTheme.typography.bodyLarge)
			Text(
				text = "${post.userId} - ${post.category}",
				style = MaterialTheme.typography.bodySmall
			)
			Text(text = post.content, style = MaterialTheme.typography.labelLarge, maxLines = 3)
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					Icons.Filled.Favorite,
					contentDescription = "좋아요",
					modifier = Modifier.size(12.dp),
					tint = Color.Red
				)
				Spacer(modifier = Modifier.width(4.dp))
				Text(
					text = post.likeCount.toString(),
					style = MaterialTheme.typography.bodySmall
				)
				Spacer(modifier = Modifier.width(16.dp))
				Icon(
					Icons.AutoMirrored.Filled.Comment,
					contentDescription = "댓글",
					modifier = Modifier.size(12.dp),
					tint = Color.Blue
				)
				Spacer(modifier = Modifier.width(4.dp))
				Text(
					text = post.commentCount.toString(),
					style = MaterialTheme.typography.bodySmall
				)
				Spacer(modifier = Modifier.weight(1f))
				Text(
					text = SimpleDateFormat(
						"yyyy.MM.dd",
						Locale.getDefault()
					).format(post.createdAt),
					style = MaterialTheme.typography.bodySmall
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterChips(
	categories: List<String>,
	selectedCategory: String,
	onCategorySelected: (String) -> Unit
) {
	Column(
		modifier = Modifier
			.padding(horizontal = 16.dp)
	) {
		LazyRow(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			contentPadding = PaddingValues(vertical = 8.dp)
		) {
			items(categories, key = { it }) { category ->
				FilterChip(
					selected = (category == selectedCategory),
					onClick = { onCategorySelected(category) },
					label = { Text(category) }
				)
			}
		}
	}
}