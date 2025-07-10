package com.openstudy.carefull.screen.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openstudy.carefull.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ranking() {

    var selectedGame by remember { mutableStateOf("스쿼트") }
    val gameTypes = listOf("스쿼트", "벤치프레스", "데드리프트", "운동1", "운동2", "운동3", "운동4", "운동5")

//더미 데이터
    val rankList = listOf(
        Rank(101, "김새싹", "스쿼트", 140),
        Rank(102, "이새싹", "벤치프레스", 50),
        Rank(103, "박새싹", "데드리프트", 64),
        Rank(104, "최새싹", "스쿼트", 75),
        Rank(105, "정새싹", "벤치프레스", 90),
        Rank(106, "조새싹", "스쿼트", 110),
        Rank(107, "윤새싹", "데드리프트", 120),
        Rank(108, "강새싹", "스쿼트", 130),
        Rank(109, "신새싹", "벤치프레스", 140),
        Rank(110, "오새싹", "데드리프트", 150),
        Rank(111, "임새싹", "스쿼트", 160),
        Rank(112, "유새싹", "벤치프레스", 170),
        Rank(113, "장새싹", "데드리프트", 180),
        Rank(114, "임새싹", "스쿼트", 190),
        Rank(115, "임새싹", "벤치프레스", 200),
        Rank(116, "임새싹", "데드리프트", 210),
        Rank(117, "가새싹", "스쿼트", 220),
        Rank(118, "나새싹", "벤치프레스", 230),
        Rank(119, "다새싹", "데드리프트", 240),
        Rank(120, "라새싹", "스쿼트", 250),
        Rank(121, "마새싹", "벤치프레스", 260),
        Rank(122, "바새싹", "데드리프트", 270),
        Rank(123, "사새싹", "스쿼트", 280),
        Rank(124, "아새싹", "벤치프레스", 290),
        Rank(125, "자새싹", "데드리프트", 300),
        Rank(126, "차새싹", "스쿼트", 310),
        Rank(127, "카새싹", "벤치프레스", 320),
        Rank(128, "타새싹", "데드리프트", 330),
        Rank(129, "파새싹", "스쿼트", 340),
        Rank(130, "하새싹", "벤치프레스", 350),
        Rank(131, "야새싹", "데드리프트", 360),
        Rank(132, "여새싹", "스쿼트", 370),
        Rank(133, "훈새싹", "벤치프레스", 380),
        Rank(134, "요새싹", "데드리프트", 390),
        Rank(135, "우새싹", "스쿼트", 400),
        Rank(136, "춘새싹", "벤치프레스", 410),
        Rank(137, "예새싹", "데드리프트", 420),
    )

    val filteredList = rankList
        .filter { it.game == selectedGame }
        .sortedByDescending { it.count }


    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(gameTypes) { game ->
                FilterChip(
                    selected = (selectedGame == game),
                    onClick = { selectedGame = game },
                    label = { Text(game) }
                )
            }
        }
        if (filteredList.isNotEmpty()) {
            val topRanker = filteredList.first()
            TopRankerItem(ranker = topRanker)
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 2.dp)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            itemsIndexed(filteredList) { index, rankItem ->
                RankListItem(
                    rankNumber = index + 1,
                    rankData = rankItem
                )
            }
        }
    }
}

// 본인순위로 나오게 변경해야함
@Composable
fun TopRankerItem(ranker: Rank) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("1위", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(16.dp))
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "프로필 사진",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = ranker.nickname, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${ranker.count} 회",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun RankListItem(
    rankNumber: Int,
    rankData: Rank
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$rankNumber",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = rankData.nickname,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = rankData.game,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Text(
                text = "${rankData.count}회",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
data class Rank(
    val id: Int,
    val nickname: String,
    val game: String,
    val count: Int
)