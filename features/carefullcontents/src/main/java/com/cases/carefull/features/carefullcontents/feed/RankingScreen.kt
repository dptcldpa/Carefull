package com.cases.carefull.features.carefullcontents.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cases.carefull.domain.model.MyRankInfo
import com.cases.carefull.domain.model.Ranker
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.features.carefullcommon.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
	viewModel: RankingViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val sports = ExerciseType.entries
	
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 16.dp)
	) {
		LazyRow(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 8.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			contentPadding = PaddingValues(vertical = 8.dp)
		) {
			items(sports) { sport ->
				FilterChip(
					selected = uiState.selectedSport == sport,
					onClick = { viewModel.onSportSelected(sport) },
					label = { Text(sport.type) }
				)
			}
		}
		HorizontalDivider(thickness = 1.dp)
		
		if (!uiState.isLoading && uiState.myRankInfo?.myRecord != null) {
			MyRankItem(myRankInfo = uiState.myRankInfo!!)
		}
		
		HorizontalDivider(thickness = 1.dp)
		if (uiState.isLoading) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				CircularProgressIndicator()
			}
		} else if (uiState.isError) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				Text("데이터를 불러오는 데 실패했습니다.")
			}
		} else if (uiState.rankingList.isEmpty()) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				Text("랭킹 데이터가 없습니다.")
			}
		} else {
			LazyColumn(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(8.dp),
				contentPadding = PaddingValues(vertical = 16.dp)
			) {
				itemsIndexed(uiState.rankingList) { index, rankItem ->
					RankListItem(
						rankNumber = index + 1,
						rankData = rankItem
					)
				}
			}
		}
	}
}

@Composable
fun MyRankItem(myRankInfo: MyRankInfo) {
	Surface(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 4.dp, bottom = 4.dp),
//		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text("${myRankInfo.rank}위", style = MaterialTheme.typography.titleLarge)
			Spacer(modifier = Modifier.width(12.dp))
			//더미 이미지
//			Image(
//				painter = painterResource(id = R.drawable.app_logo),
//				contentDescription = "프로필 사진",
//				modifier = Modifier
//					.size(40.dp)
//					.clip(CircleShape)
//			)
			Spacer(modifier = Modifier.width(12.dp))
			Text(
				myRankInfo.myRecord?.userId ?: "",
				style = MaterialTheme.typography.titleMedium
			)
			Spacer(modifier = Modifier.weight(1f))
			Text(
				"${myRankInfo.myRecord?.totalCount ?: 0} 회",
				style = MaterialTheme.typography.titleMedium
			)
		}
	}
}

@Composable
fun RankListItem(
	rankNumber: Int,
	rankData: Ranker
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
,
		colors = CardDefaults.cardColors(containerColor = Color.White),
		border = BorderStroke(1.dp, color = Color.LightGray)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = "${rankNumber}위",
				style = MaterialTheme.typography.titleMedium,
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
			}
			Text(
				text = "${rankData.totalCount} 회",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.primary,
				fontWeight = FontWeight.SemiBold
			)
		}
	}
}