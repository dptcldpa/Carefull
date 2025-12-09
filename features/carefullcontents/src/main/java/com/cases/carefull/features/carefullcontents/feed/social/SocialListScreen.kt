package com.cases.carefull.features.carefullcontents.feed.social


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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cases.carefull.domain.model.feed.Post
import com.cases.carefull.domain.model.feed.SocialCategory
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.components.CommonFilterChipRow
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
    val categories = SocialCategory.entries
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadPosts(true,uiState.selectedCategory)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            CommonFilterChipRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = categories,
                selectedItem = uiState.selectedCategory,
                onItemSelected = { newCategory ->
                    viewModel.loadPosts(true,newCategory)
                },
                itemLabel = { it.category },
                itemKey = { it.name }
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.error != null) {
                    Text(text = stringResource(R.string.error_generic_format, uiState.error!!))
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
            Icon(
                Icons.Default.Create,
                contentDescription = stringResource(R.string.post_create_new)
            )
        }

    }
}

@Composable
fun PostList(posts: List<Post>, onPostClick: (String) -> Unit) {
    if (posts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.error_no_post))
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
                text = stringResource(
                    R.string.post_info_format_user_category2,
                    post.userId,
                    post.category.category
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(text = post.content, style = MaterialTheme.typography.labelLarge, maxLines = 3)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = stringResource(R.string.common_like),
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
                    contentDescription = stringResource(R.string.common_comment),
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
                        stringResource(R.string.date_format_date_only),
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
    categories: List<SocialCategory>,
    selectedCategory: SocialCategory,
    onCategorySelected: (SocialCategory) -> Unit
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
            items(categories, key = { it.name }) {
                FilterChip(
                    selected = (it == selectedCategory),
                    onClick = { onCategorySelected(it) },
                    label = { Text(it.category) }
                )
            }
        }
    }
}
