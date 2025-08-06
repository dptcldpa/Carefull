package com.cases.carefull.features.carefullcontents.routine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.cases.carefull.domain.model.DietInfo
import com.cases.carefull.domain.model.MealType
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute

@Composable
fun DietSearchScreen(
	viewModel: DietViewModel,
	navController: NavController
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	var foodNameInput by remember { mutableStateOf("새우") }
	
	val route = navController.currentBackStackEntry?.toRoute<RoutineRoute.DietSearchScreen>()
	val mealType = route?.mealType
	
	if (mealType == null) {
		Text("오류: 식사 정보가 없습니다.")
		return
	}
	
	var foodToEdit by remember { mutableStateOf<DietInfo?>(null) }
	
	foodToEdit?.let { food ->
		EditWeightDialog(
			item = food,
			onConfirm = { newWeight ->
				viewModel.onAddMeal(
					dietInfo = food,
					mealType = MealType.valueOf(mealType),
					newWeight = newWeight
				)
				foodToEdit = null
				navController.popBackStack()
			},
			onDismiss = {
				foodToEdit = null
			},
		)
	}
	
	Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
		Column(
			modifier = Modifier.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			OutlinedTextField(
				value = foodNameInput,
				onValueChange = { foodNameInput = it },
				label = { Text("음식 이름") },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true
			)
			Spacer(Modifier.height(8.dp))
			Button(
				onClick = { viewModel.onSearch(foodNameInput) },
				enabled = !uiState.isLoading,
				modifier = Modifier.fillMaxWidth()
			) {
				Text("검색")
			}
			Spacer(Modifier.height(16.dp))
			
			// 결과 표시 영역
			if (uiState.isLoading) {
				CircularProgressIndicator()
			} else {
				LazyColumn(modifier = Modifier.fillMaxSize()) {
					items(uiState.searchResults) { foodItem ->
						FoodItemCard(
							item = foodItem,
							onClick = {
								foodToEdit = foodItem
							}
						)
					}
				}
			}
		}
	}
}

@Composable
fun FoodItemCard(
	item: DietInfo,
	onClick: () -> Unit,
) {
	Card(
		onClick = onClick,
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(modifier = Modifier.fillMaxSize()) {
				Text(text = item.name ?: "이름 없음", style = MaterialTheme.typography.titleMedium)
				
				Column(
					modifier = Modifier.fillMaxSize(),
					horizontalAlignment = Alignment.End
				) {
					Text("1회 제공량: ${item.weight ?: "정보없음"}g")
					Text("칼로리: ${item.calories ?: "정보 없음"} kcal")
				}
			}
			Row(modifier = Modifier.fillMaxSize()) {
				Text("탄수화물: ${item.carbs ?: "정보 없음"}g ")
				Text("단백질: ${item.proteins ?: "정보 없음"}g ")
				Text("지방: ${item.fats ?: "정보 없음"}g")
			}
		}
	}
}

@Composable
fun EditWeightDialog(
	item: DietInfo,
	onConfirm: (Int) -> Unit, // "확인" 버튼 눌렀을 때 호출, 새 중량을 전달
	onDismiss: () -> Unit      // "취소" 또는 바깥 영역 클릭 시 호출
) {
	var weight by remember { mutableStateOf(item.weight?.toString() ?: "") }
	
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(text = "${item.name} 중량 수정") },
		text = {
			OutlinedTextField(
				value = weight,
				onValueChange = { newValue ->
					if (newValue.all { it.isDigit() }) {
						weight = newValue
					}
				},
				label = { Text("새로운 중량 (g)") },
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
			)
		},
		confirmButton = {
			Button(
				onClick = {
					val newWeight = weight.toIntOrNull()
					if (newWeight != null) {
						onConfirm(newWeight)
					}
				},
				enabled = weight.isNotBlank()
			) {
				Text("확인")
			}
		},
		dismissButton = {
			Button(onClick = onDismiss) {
				Text("취소")
			}
		}
	)
}