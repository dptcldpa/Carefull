package com.cases.carefull.features.carefullcontents.feed


import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
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
import androidx.compose.material3.Scaffold
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
	val categories = listOf("전체", "운동", "식단", "의료")
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
	Scaffold(
		floatingActionButton = {
			FloatingActionButton(
				onClick = {
					navController.navigate(FeedRoute.CreatePostScreen())
				}
			) {
				Icon(Icons.Default.Create, contentDescription = "글쓰기")
			}
		}
	) { paddingValues ->
		Column(modifier = Modifier.padding(paddingValues)) {
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
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			items(posts) { post ->
				PostItem(post = post, onClick = { onPostClick(post.id) })
			}
		}
	}
}

@Composable
fun PostItem(post: Post, onClick: () -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		elevation = CardDefaults.cardElevation(4.dp)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
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
			Text(text = post.title, style = MaterialTheme.typography.titleLarge)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = "작성자: ${post.userId} | 카테고리: ${post.category}",
				style = MaterialTheme.typography.bodySmall
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(text = post.content, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
			Spacer(modifier = Modifier.height(8.dp))
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(Icons.Filled.Favorite, contentDescription = "좋아요", tint = Color.Red)
				Spacer(modifier = Modifier.width(4.dp))
				Text(text = post.likeCount.toString())
				Spacer(modifier = Modifier.width(16.dp))
				Icon(Icons.Filled.Comment, contentDescription = "댓글")
				Spacer(modifier = Modifier.width(4.dp))
				Text(text = post.commentCount.toString())
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
	// Row를 수평 스크롤 가능하게 만듭니다.
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.horizontalScroll(rememberScrollState())
			.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		categories.forEach { category ->
			FilterChip(
				selected = (category == selectedCategory),
				onClick = { onCategorySelected(category) },
				label = { Text(category) }
			)
		}
	}
}