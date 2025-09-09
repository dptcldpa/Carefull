package com.cases.carefull.features.carefullcontents.diagnosis.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.features.carefullcommon.R


@Composable
fun MedicineInfoScreen(
	medicineItem: MedicineItem
) {
	val scrollState = rememberScrollState()
	
	var selectedTab by remember { mutableStateOf(0) }
	val tabs = listOf("사용방법", "주의사항", "상호작용", "부작용", "보관 방법", "효능효과")
	
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(scrollState)
			.padding(20.dp)
	) {
		Text(
			text =  medicineItem.itemName ?: "정보 없음",
			style = MaterialTheme.typography.titleLarge
		)
		
		Spacer(modifier = Modifier.height(13.dp))
		
		AsyncImage(
			model = medicineItem.itemImage,
			contentDescription = medicineItem.itemName,
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(12.dp))
				.background(Color.LightGray),
			contentScale = ContentScale.FillWidth,
			error = painterResource(id = R.drawable.ic_launcher_background)
		)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		ScrollableTabRow(
			selectedTabIndex = selectedTab,
			edgePadding = 0.dp,
			containerColor = Color.Transparent,
			contentColor = Color.Transparent,
			indicator = {},
			divider = {}
		) {
			tabs.forEachIndexed { index, title ->
				Tab(
					selected = selectedTab == index,
					onClick = { selectedTab = index },
					selectedContentColor = Color.White,
					unselectedContentColor = Color.Black,
					text = {
						Box(
							modifier = Modifier
								.background(
									color = if (selectedTab == index) Color(0xFF00C73C) else Color(
										0xFFF5F5F5
									),
									shape = RoundedCornerShape(50)
								)
								.padding(horizontal = 16.dp, vertical = 8.dp)
						) {
							Text(
								text = title,
								color = if (selectedTab == index) Color.White else Color.Black
							)
						}
					}
				)
			}
		}
		
		Spacer(modifier = Modifier.height(8.dp))
		
		when (selectedTab) {
			0 -> {
				InfoBlock(label = "사용 방법", value = medicineItem.useMethodQesitm)
			}
			
			1 -> {
				InfoBlock(label = "주의 사항", value = medicineItem.atpnQesitm)
			}
			
			2 -> {
				InfoBlock(label = "상호작용", value = medicineItem.intrcQesitm)
			}
			
			3 -> {
				InfoBlock(label = "부작용", value = medicineItem.seQesitm)
			}
			
			4 -> {
				InfoBlock(label = "보관 방법", value = medicineItem.depositMethodQesitm)
			}
			
			5 -> {
				InfoBlock(label = "효능효과", value = medicineItem.efcyQesitm)
			}
		}
	}
}

@Composable
private fun InfoBlock(label: String, value: String?) {
	Column(modifier = Modifier.padding(vertical = 4.dp)) {
		Text(
			text = label,
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.primary
		)
		Text(
			text = value ?: "정보 없음",
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier
				.padding(
					start = 8.dp,
					top = 8.dp,
					bottom = 8.dp
				)
		)
	}
}