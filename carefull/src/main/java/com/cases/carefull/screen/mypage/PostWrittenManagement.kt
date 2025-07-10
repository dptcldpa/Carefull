package com.openstudy.carefull.screen.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openstudy.carefull.R
import com.openstudy.carefull.common.BottomNavigationBar
import com.openstudy.carefull.common.RowLine
import com.openstudy.carefull.ui.theme.CarefullTheme

@Composable
fun PostWrittenManagement() {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentRoute = R.string.mypage)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.writing_management),
                style = MaterialTheme.typography.titleLarge
            )
            RowLine()
            PostList()
        }
    }
}


// 더미데이터 카테고리 추가할것
data class Post(
    val postNumber: Int, // 글번호
    val title: String, // 글 제목
    val content: String, // 글 내용
    val createdAt: String, // 작성일 (예: "2024.05.21")
    val commentCount: Int, // 댓글 수
    val likeCount: Int, // 좋아요 수
)

object DummyData {
    fun getDummyPosts(): List<Post> {
        return listOf(
            Post(123, "점심 메뉴 추천 받습니다.", "강남역 근처 맛집 아시는 분?", "2025.06.11", 5, 12),
            Post(124, "점심 메뉴 고민입니다.", "라면 질렸어요.", "2025.06.14", 2, 4),
            Post(125, "아침 뭐 드시나요?", "귀찮은데 쉽고 빠르게 준비해 먹을 수 있는 메뉴 있을까요?", "2025.06.17", 8, 21),
            Post(162, "너무 배고픕니다.", "만들어 먹을 힘이 없어요. 동네 중국집 추천해주세요.", "2025.06.19", 8, 32),
            Post(173, "놀다가 팔이 부러졌어요...", "지금은 크게 안 아픈데 나중에 병원가도 될까요?", "2025.06.22", 22, 53),
            Post(13, "놀다가 다리가 부러졌어요...", "지금은 크게 안 아픈데 나중에 병원가도 될까요?", "2025.06.23", 32, 66),
            Post(15, "놀다가 허리가 부러졌어요...", "지금은 크게 안 아픈데 나중에 병원가도 될까요?", "2025.06.24", 48, 67),
            Post(134, "놀다가 어깨가 부러졌어요...", "지금은 크게 안 아픈데 나중에 병원가도 될까요?", "2025.06.25", 62, 153),
        )
    }
}

@Composable
fun PostList() {
    val posts = DummyData.getDummyPosts()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = posts,
            key = { post -> post.postNumber }
        ) { post ->
            PostItem(post = post)
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = post.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(0.5f))
            IconButton(onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Column(modifier = Modifier.padding(4.dp)) {
            Text(text = post.content, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "작성일: ", style = MaterialTheme.typography.bodySmall)
                Text(text = post.createdAt, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.fillMaxSize(0.35f))
                Text(text = "댓글: ${post.commentCount}", style = MaterialTheme.typography.bodySmall)
                Text(text = " 좋아요: ${post.likeCount}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostWrittenManagementPreview() {
    CarefullTheme {
        PostWrittenManagement()
    }
}