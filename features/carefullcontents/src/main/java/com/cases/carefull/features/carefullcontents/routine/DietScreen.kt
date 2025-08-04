package com.cases.carefull.features.carefullcontents.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute


@Composable
fun DietScreen(    viewModel: DietViewModel,
				   sharedViewModel: SharedViewModel,
				   navController: NavController
) {
	val uiState = viewModel.uiState.collectAsState()
	val searchResult by sharedViewModel.searchResult.collectAsState()
	
	LaunchedEffect(searchResult) {
		searchResult?.let { result ->
			val mealType = MealType.valueOf(result.mealType)
			val foodData = result.selectedFood
			val newMealRecord = MealRecord(
				// id는 MealRecord의 기본값을 사용하거나, foodData의 고유 코드를 사용할 수 있습니다.
				// 여기서는 FoodData의 foodCode를 사용하고, 없으면 현재 시간으로 대체합니다.
				id = foodData.name?.hashCode()?.toLong() ?: System.currentTimeMillis(),
				name = foodData.name ?: "이름 없음",
				calories = foodData.kcal?.toDoubleOrNull()?.toInt() ?: 0, // String을 Int로 안전하게 변환
				mealType = mealType // 어떤 식사 유형인지 지정
			)
			
			// 3. 이제 올바른 타입의 인자로 onAddMeal 함수를 호출합니다.
			viewModel.onAddMeal(newMealRecord)
			
			// 4. 결과 처리가 끝났으므로, SharedViewModel의 상태를 초기화하여 중복 추가 방지
			sharedViewModel.clearSearchResult()
		}
	}
	
	Column {
		Spacer(modifier = Modifier.height(24.dp))
		Text(
			text = "오늘 식사 내역", style = MaterialTheme.typography.bodyLarge,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			textAlign = TextAlign.Center
		)
		Text(
			text = "총 ${uiState.value.totalCalories} 칼로리",
			style = MaterialTheme.typography.bodyLarge,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			textAlign = TextAlign.Center
		)
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
		) {
			item {
				MealType.entries.forEach { mealType ->
					MealSection(
						mealType = mealType,
						addedFoods = uiState.value.mealsByTime[mealType] ?: emptyList(),
						onCameraClick = {},
						onAddClick = {navController.navigate(RoutineRoute.DietSearchScreen(mealType = mealType.name))
						},
						onRemoveClick = {mealRecordToRemove ->
							viewModel.onRemoveFood(mealRecordToRemove)}
					
					)
				}
			}
		}
	}
}



@Composable
fun MealSection(
	mealType: MealType,
	addedFoods: List<MealRecord>,
	onCameraClick: () -> Unit,
	onAddClick: () -> Unit,
	onRemoveClick: (MealRecord) -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 12.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = mealType.time,
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Bold
				)
				Spacer(modifier = Modifier.weight(1f))
				IconButton(onClick = onCameraClick) {
					Icon(
						imageVector = Icons.Default.Camera,
						contentDescription = "${mealType.time} 음식 추가",
						tint = MaterialTheme.colorScheme.primary,
						modifier = Modifier.size(28.dp)
					)
				}
				IconButton(onClick = onAddClick) {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = "${mealType.time} 음식 검색",
						tint = MaterialTheme.colorScheme.primary,
						modifier = Modifier.size(28.dp)
					)
				}
			}
			HorizontalDivider(
				thickness = 1.dp,
				color = Color.Black
			)
			if (addedFoods.isNotEmpty()) {
				Column(
					modifier = Modifier.fillMaxWidth(),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					addedFoods.forEach { addedFood ->
						FoodItemRow(
							food = addedFood,
							onRemove = { onRemoveClick(addedFood) }
						)
					}
				}
			} else {
				Text(
					text = "아직 추가된 음식이 없습니다.",
					style = MaterialTheme.typography.bodyMedium,
					color = Color.Gray,
					modifier = Modifier.padding(8.dp)
				)
			}
		}
	}
}

@Composable
fun FoodItemRow(food: MealRecord, onRemove: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = "${food.name} (${food.calories} kcal)",
			modifier = Modifier.weight(1f)
		)
		IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
			Icon(imageVector = Icons.Default.Close, contentDescription = "삭제")
		}
	}
}