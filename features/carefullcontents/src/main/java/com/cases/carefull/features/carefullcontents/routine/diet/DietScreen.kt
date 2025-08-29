package com.cases.carefull.features.carefullcontents.routine.diet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute

@Composable
fun DietScreen(
	viewModel: DietViewModel,
	navController: NavController
) {
	val uiState = viewModel.uiState.collectAsStateWithLifecycle()
	
	Column {
		if (uiState.value.isLoading) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				CircularProgressIndicator()
			}
		}
		Text(
			text = "기초대사량 : ${uiState.value.bmrState.calculatedBmr}kcal",
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			textAlign = TextAlign.Center
		)
		Text(
			text = "활동대사량 : ${uiState.value.bmrState.activityMetabolism}kcal",
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			textAlign = TextAlign.Center
		)
		Text(
			text = "현재 섭취량: ${uiState.value.totalCalories}kcal",
			style = MaterialTheme.typography.bodyLarge,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			textAlign = TextAlign.Center
		)
		Text(
			text = "지방 : ${uiState.value.totalFats}g",
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			textAlign = TextAlign.Center
		)
		Text(
			text = "탄수화물 : ${uiState.value.totalCarbs}g",
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			textAlign = TextAlign.Center
		)
		Text(
			text = "단백질 : ${uiState.value.totalProteins}g",
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			textAlign = TextAlign.Center
		)
		Text(
			text = "지방 : ${uiState.value.totalFats}g",
			style = MaterialTheme.typography.bodySmall,
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
						onAddClick = {
							navController.navigate(RoutineRoute.DietSearchScreen(mealType = mealType.name))
						},
						onRemoveClick = { mealRecordToRemove ->
							viewModel.onRemoveMeal(mealRecordToRemove)
						}
					)
				}
			}
		}
	}
}

@Composable
fun MealSection(
	mealType: MealType,
	addedFoods: List<DietCollection>,
	onCameraClick: () -> Unit,
	onAddClick: () -> Unit,
	onRemoveClick: (DietCollection) -> Unit
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
fun FoodItemRow(food: DietCollection, onRemove: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = "${food.mealName} ${food.weight}g (${food.kcal} kcal)",
			modifier = Modifier.weight(1f)
		)
		IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
			Icon(imageVector = Icons.Default.Close, contentDescription = "삭제")
		}
	}
}