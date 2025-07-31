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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.cases.carefull.data.network.FoodData
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun DietSearchScreen(
	navController: NavController,
	sharedViewModel: SharedViewModel,
	searchViewModel: FoodSearchViewModel
) {
	val route = navController.currentBackStackEntry?.toRoute<RoutineRoute.DietSearchScreen>()
	val mealType = route?.mealType
	
	if (mealType == null) {
		Text("오류: 식사 유형 정보가 없습니다.")
		return
	}
	
	val uiState by searchViewModel.uiState.collectAsState()
	var foodNameInput by remember { mutableStateOf("새우") }
	
	var foodList by remember { mutableStateOf<List<FoodData>>(emptyList()) }
	var errorMessage by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(false) }
	
	val API_KEY = ""
	
	Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
		Column(
			modifier = Modifier.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(Modifier.height(50.dp))
			OutlinedTextField(
				value = foodNameInput,
				onValueChange = { foodNameInput = it },
				label = { Text("음식 이름") },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true
			)
			Spacer(Modifier.height(8.dp))
			Button(
				onClick = { searchViewModel.searchFood(API_KEY, foodNameInput) },
				enabled = !isLoading,
				modifier = Modifier.fillMaxWidth()
			) {
				Text("검색")
			}
			Spacer(Modifier.height(16.dp))
			
			// 결과 표시 영역
			if (uiState.isLoading) {
				CircularProgressIndicator()
			} else if (uiState.errorMessage != null) {
				Text(text = uiState.errorMessage!!, color = Color.Red)
			} else {
				LazyColumn(modifier = Modifier.fillMaxSize()) {
					// ViewModel의 foodList 상태 사용
					items(uiState.foodList) { foodItem ->
						FoodItemCard(
							item = foodItem,
							onClick = {
								val result = FoodSearchResult(
									mealType = mealType,
									selectedFood = foodItem
								)
								sharedViewModel.postSearchResult(result)
								navController.popBackStack()
							}
						)
					}
				}
			}
		}
	}
}

@Composable
fun FoodItemCard(item: FoodData, onClick: () -> Unit) {
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
					Text("1회 제공량: ${item.serving ?: "정보없음"}")
					Text("칼로리: ${item.kcal ?: "정보 없음"} kcal")
				}
			}
			Row(modifier = Modifier.fillMaxSize()) {
				Text("탄수화물: ${item.carbohydrate ?: "정보 없음"}g")
				Spacer(modifier = Modifier.fillMaxSize(0.6f))
				Text("당류: ${item.carbohydrateSugar ?: "정보 없음"}g")
			}
			Row(modifier = Modifier.fillMaxSize()) {
				Text("단백질: ${item.protein ?: "정보 없음"}g")
			}
			
			Row(modifier = Modifier.fillMaxSize()) {
				Text("지방: ${item.fat ?: "정보 없음"}g")
				Spacer(modifier = Modifier.fillMaxSize(0.6f))
				Text("포화지방: ${item.saturatedFat ?: "정보없음"}g")
			}
			Text("나트륨: ${item.sodium ?: "정보 없음"}mg")
		}
	}
}



class SharedViewModel : ViewModel() {
	// 검색 결과를 담는 StateFlow. 초기값은 null.
	private val _searchResult = MutableStateFlow<FoodSearchResult?>(null)
	val searchResult = _searchResult.asStateFlow()
	
	// 검색 화면에서 이 함수를 호출하여 결과를 설정
	fun postSearchResult(result: FoodSearchResult) {
		_searchResult.value = result
	}
	
	// DietScreen에서 결과를 소비한 후, 이 함수를 호출하여 State를 초기화 (중복 처리 방지)
	fun clearSearchResult() {
		_searchResult.value = null
	}
}