package com.openstudy.carefull.screen.routine

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openstudy.carefull.ui.theme.CarefullTheme

@Composable
fun SearchFood() {
    var search by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Food>>(emptyList()) }

    LaunchedEffect(key1 = search) {
        searchResults = searchDummyFood(search)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "음식 검색",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    "검색하기",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            placeholder = {
                Text(
                    "음식을 검색하세요.",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            trailingIcon = {
                if (search.isNotBlank()) {
                    IconButton(
                        onClick = {
                            search = ""
                        }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                } else {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = TextFieldDefaults.shape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,   // 포커스 배경색
                unfocusedContainerColor = Color.White, // 포커스 안 됐을 때 배경색
                disabledContainerColor = Color.White,  // 비활성화 배경색
            )
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = searchResults,
                key = { it.id }) { food -> // searchResults 리스트를 순회하며 각 food에 대해 그림
                FoodSearchResultItem(
                    food = food,
                    onAddClick = {
                        // '추가' 버튼 클릭 시 실행할 로직
                        // 예: onFoodAdded(food), navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun FoodSearchResultItem(
    food: Food, // 표시할 Food 데이터
    onAddClick: () -> Unit // '추가' 버튼 클릭 이벤트 콜백
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. 음식 이름과 추가 버튼
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.bodyLarge,// "치즈 햄버거"
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // 2. 구분선
            HorizontalDivider()

            // 3. 나머지 추가 정보 나열
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround // 공간을 균등하게 분배
            ) {
                NutritionInfo(label = "칼로리", value = "${food.calories} kcal")
                NutritionInfo(label = "탄수화물", value = "${food.carbs} g")
                NutritionInfo(label = "단백질", value = "${food.protein} g")
                NutritionInfo(label = "지방", value = "${food.fat} g")
            }
        }
    }
}

fun searchDummyFood(query: String): List<Food> {
    val hamburger = Food(
        id = 101,
        name = "hamburger",
        calories = 550,
        carbs = 45,
        protein = 25,
        fat = 30
    )
    return if (query.isNotBlank() && hamburger.name.contains(query, ignoreCase = true)) {
        listOf(hamburger)
    } else {
        emptyList()
    }
}


// 영양 정보를 보여주는 작은 헬퍼 컴포저블
@Composable
fun NutritionInfo(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

data class Food(
    val id: Int,
    val name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int
)

@Preview(showBackground = true)
@Composable
fun SearchFoodPreview() {
    CarefullTheme {
        SearchFood()
    }
}