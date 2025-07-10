package com.openstudy.carefull.screen.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.openstudy.carefull.R
import com.openstudy.carefull.screen.mypage.Post

@Composable
fun Social() {
    var selectedSocial by remember { mutableStateOf("전체") }
    val socialTypes = listOf("전체", "운동", "식단", "병원")

//더미데이터
    val postList = listOf(
        SocialPost(
            1,
            "운동매니아", 1101,
            "오늘 스쿼트 3대 500 찍었습니다!",
            "정말 힘든 하루였지만 보람차네요. 여러분도 득근하세요! #오운완 #헬스타그램",
            12,
            53,
            "운동",
            "2024.05.21"
        ),
        SocialPost(
            2,
            "다이어터",
            1102,
            "식단 공유합니다~",
            "아침: 그릭요거트, 점심: 닭가슴살 샐러드, 저녁: 두부. 배고프지만 참아봅니다.",
            8,
            32,
            "식단",
            "2024.05.21"
        ),
        SocialPost(
            3,
            "헬린이",
            1103,
            "벤치프레스 자세 질문있어요",
            "가슴에 자극이 잘 안 오는데, 팁 좀 알려주실 수 있을까요? 영상 첨부합니다...",
            25,
            15,
            "운동",
            "2024.05.20"
        ),
        SocialPost(
            4,
            "요가사랑",
            1104,
            "아침 요가 루틴",
            "상쾌한 아침을 여는 10분 요가 루틴입니다. 따라해보세요!",
            5,
            41,
            "운동",
            "2024.05.20"
        ),
        SocialPost(5, "허리환자", 1105, "허리근황", "아파요..", 15, 51, "병원", "2024.05.19")
    )

    val filteredList = if (selectedSocial == "전체") {
        postList
    } else {
        postList.filter { it.category == selectedSocial }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(socialTypes) { game ->
                FilterChip(
                    selected = (selectedSocial == game),
                    onClick = { selectedSocial = game },
                    label = { Text(game) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList) { post ->
                PostCardItem(
                    post = post,
                    onCardClick = { clickedPost ->
                        {}
                    }
                )
            }
        }
    }
}

@Composable
fun PostCardItem(
    post: SocialPost,
    onCardClick: (Post) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {},
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {
            // 프로필 정보
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo), // 임시 프로필 이미지
                    contentDescription = "작성자 프로필 사진",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.authorNickname, fontWeight = FontWeight.Bold)
                    Text(
                        text = post.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 글 제목 및 내용
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3, // 내용이 너무 길면 3줄까지
                overflow = TextOverflow.Ellipsis // ...으로 생략
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 좋아요 및 댓글 수
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = post.category,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 1.dp)
                )
                IconWithText(
                    icon = Icons.Default.FavoriteBorder,
                    text = post.likeCount.toString()
                )
                IconWithText(
                    icon = Icons.AutoMirrored.Filled.Comment,
                    text = post.commentCount.toString()
                )
            }
        }
    }
}

@Composable
internal fun SocialFab(
    isExpanded: Boolean,
    onFabClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {

    Column(horizontalAlignment = Alignment.End) {
        if (isExpanded) {
            val categories = listOf("운동", "식단", "병원")
            categories.forEach { category ->
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(8.dp),
                    onClick = { onCategoryClick(category) },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    text = {
                        Text(
                            category,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                )
            }
        }
        // 메인 FAB
        FloatingActionButton(onClick = onFabClick) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Edit,
                contentDescription = "글쓰기"
            )
        }
    }
}

@Composable
private fun IconWithText(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

data class SocialPost(
    val authorId: Int,
    val authorNickname: String,
    val postId: Int,
    val title: String,
    val content: String,
    val commentCount: Int,
    val likeCount: Int,
    val category: String,
    val date: String
)