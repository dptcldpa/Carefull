package com.cases.carefull.features.carefullcontents.routine.diet

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun FoodInformation() {
    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(top = 5.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "음식 정보",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    modifier = Modifier,
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                HorizontalDivider(
                    modifier = Modifier,
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp),

                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White
                    ),
                ) {
                    Text(
                        text = "등록하기",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.4f)
                    .background(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(
                modifier = Modifier,
                thickness = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                //더미데이터
                item {
                    FoodSearchResultItem(
                        food = Food(101, "햄버거", 550, 45, 25, 30),
                        onAddClick = {})
                    FoodSearchResultItem(
                        food = Food(103, "치즈버거", 650, 48, 28, 35),
                        onAddClick = {})
                    FoodSearchResultItem(
                        food = Food(104, "더블버거", 850, 50, 40, 50),
                        onAddClick = {})
                    FoodSearchResultItem(
                        food = Food(105, "새우버거", 480, 55, 18, 22),
                        onAddClick = {})
                    FoodSearchResultItem(
                        food = Food(106, "치킨버거", 520, 47, 26, 25),
                        onAddClick = {})
                }
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