package com.cases.carefull.features.carefullcontents.routine.diet

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.FavoriteMeal
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.domain.model.diet.RecentMealSearch
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DietSearchScreen(
    viewModel: DietViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val route = navController.currentBackStackEntry?.toRoute<RoutineRoute.DietSearchScreen>()
    val mealType = route?.mealType
    var foodToEdit by remember { mutableStateOf<DietCollection?>(null) }
    var showDirectInputDialog by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()

    if (mealType == null) {
        return
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateBackToDietScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }

    foodToEdit?.let { food ->
        EditWeightDialog(
            item = food,
            onConfirm = { newWeight ->
                viewModel.onAddMeal(
                    dietCollection = food,
                    mealType = MealType.valueOf(mealType),
                    updateWeight = newWeight
                )
                foodToEdit = null
                navController.popBackStack()
            },
            onDismiss = {
                foodToEdit = null
            },
        )
    }

    if (uiState.isFavoritesDialogVisible) {
        FavoritesDialog(
            favorites = uiState.favoriteMeals,
            onDismiss = { viewModel.hideFavoritesDialog() },
            onSelect = { favorite ->
                viewModel.onFavoriteMealClicked(favorite)
            },
            onDelete = { viewModel.deleteFavoriteMeal(it) }
        )
    }

    uiState.selectedFavoriteForEditing?.let { favoriteMeal ->
        val dietCollectionForItem = DietCollection(
            mealName = favoriteMeal.name,
            weight = favoriteMeal.weight,
            kcal = favoriteMeal.kcal,
            carbohydrate = favoriteMeal.carbohydrate,
            protein = favoriteMeal.protein,
            fat = favoriteMeal.fat
        )

        EditWeightDialog(
            item = dietCollectionForItem,
            onConfirm = { newWeight ->
                viewModel.onFavoriteMealWeightConfirmed(newWeight, MealType.valueOf(mealType))
            },
            onDismiss = {
                viewModel.dismissEditWeightDialog()
            }
        )
    }

    if (showDirectInputDialog) {
        CustomInputDialog(
            onConfirm = { newFood, isFavorite ->
                viewModel.onCustomMealConfirmed(
                    meal = newFood,
                    isFavorite = isFavorite
                )
                showDirectInputDialog = false
            },
            onDismiss = {
                showDirectInputDialog = false
            }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                value = searchQuery,
                onValueChange = { newQuery ->
                    viewModel.onSearchQueryChanged(newQuery)
                },
                onSearch = { viewModel.onRecentMealSearch() }
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { viewModel.showFavoritesDialog() },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = Color.White
                    )
                ) {
                    Text("즐겨찾기")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = { showDirectInputDialog = true },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = Color.White
                    )
                ) {
                    Text("직접 입력하기")
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    if (uiState.searchResults.isEmpty() && searchQuery.isBlank()) {
                        RecentMealSearchesSection(
                            searches = uiState.recentSearches,
                            onDeleteAllRecentMealSearches = { viewModel.onClearAllRecentMealSearches() },
                            onItemClick = { name -> viewModel.onRecentSearchClicked(name) },
                            onItemDelete = { search -> viewModel.onDeleteRecentSearch(search) }
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.searchResults) { foodItem ->
                                FoodItemCard(
                                    item = foodItem,
                                    onClick = { foodToEdit = foodItem }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    item: DietCollection,
    onClick: () -> Unit,
) {
    OutlinedCard(
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Surface(color = Color.White) {
            Column(
                modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = item.mealName,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "(${item.weight}g)",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        "${item.kcal} kcal",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "탄수화물: ${item.carbohydrate}g ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "단백질: ${item.protein}g ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "지방: ${item.fat}g",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EditWeightDialog(
    item: DietCollection,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var weight by remember { mutableStateOf(item.weight.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "${item.mealName} 중량 수정") },
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

@Composable
fun CustomInputDialog(
    onConfirm: (DietCollection, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var carbohydrate by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var calculatedKcal by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }
    val isConfirmEnabled =
        name.isNotBlank() &&
                weight.isDigitsOnly() &&
                calculatedKcal.isDigitsOnly() &&
                carbohydrate.isDigitsOnly() &&
                protein.isDigitsOnly() &&
                fat.isDigitsOnly()

    LaunchedEffect(carbohydrate, protein, fat) {
        val carbsGram = carbohydrate.toIntOrNull() ?: 0
        val proteinGram = protein.toIntOrNull() ?: 0
        val fatGram = fat.toIntOrNull() ?: 0
        val totalKcal = (carbsGram * 4) + (proteinGram * 4) + (fatGram * 9)
        calculatedKcal = totalKcal.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "음식 정보 직접 입력") },
        text = {
            LazyColumn {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("음식 이름") },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("중량 (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = carbohydrate,
                        onValueChange = { carbohydrate = it },
                        label = { Text("탄수화물 (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = { Text("단백질 (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { fat = it },
                        label = { Text("지방 (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = calculatedKcal,
                        onValueChange = {},
                        label = { Text("칼로리 (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newMeal = DietCollection(
                        mealName = name,
                        weight = weight.toInt(),
                        kcal = calculatedKcal.toInt(),
                        carbohydrate = carbohydrate.toInt(),
                        protein = protein.toInt(),
                        fat = fat.toInt()
                    )
                    onConfirm(newMeal, isFavorite)
                },
                enabled = isConfirmEnabled
            ) {
                Text("등록하기")
            }
        },
        dismissButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isFavorite,
                    onCheckedChange = { isFavorite = it }
                )
                Text("즐겨찾기", modifier = Modifier.clickable { isFavorite = !isFavorite })

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = onDismiss) {
                    Text("취소")
                }
            }
        }
    )
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge,
            label = { Text("음식 검색") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            placeholder = { Text("음식을 입력하세요.") },
//			leadingIcon = {
//				Icon(
//					imageVector = Icons.Default.Search,
//					contentDescription = "검색 아이콘"
//				)
//			},
            trailingIcon = {
                if (value.isNotBlank()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "입력 초기화"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch()
                keyboardController?.hide()
            }
            ),
            shape = RoundedCornerShape(16.dp)
        )
        IconButton(
            modifier = Modifier.padding(top = 10.dp),
            onClick = {
                onSearch()
                keyboardController?.hide()
            }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "검색 아이콘"
            )
        }
    }
}

@Composable
fun FavoritesDialog(
    favorites: List<FavoriteMeal>,
    onDismiss: () -> Unit,
    onSelect: (FavoriteMeal) -> Unit,
    onDelete: (FavoriteMeal) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("즐겨찾기 목록") },
        text = {
            if (favorites.isEmpty()) {
                Text("즐겨찾기된 음식이 없습니다.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(favorites, key = { it.id }) { meal ->
                        FavoriteItemRow(
                            meal = meal,
                            onSelect = { onSelect(meal) },
                            onDelete = { onDelete(meal) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        }
    )
}

@Composable
private fun FavoriteItemRow(
    meal: FavoriteMeal,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onSelect)
			.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(meal.name, fontWeight = FontWeight.Bold)
            Text(
                "${meal.kcal} kcal, ${meal.weight}g 탄 ${meal.carbohydrate}g, 단 ${meal.protein}g, 지 ${meal.fat}g",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, contentDescription = "삭제")
        }
    }
}

@Composable
fun RecentMealSearchesSection(
    searches: List<RecentMealSearch>,
    onDeleteAllRecentMealSearches: () -> Unit,
    onItemClick: (String) -> Unit,
    onItemDelete: (RecentMealSearch) -> Unit
) {
    if (searches.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("최근 검색 기록이 없습니다.", color = Color.Gray)
        }
    } else {
        Column(
            modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "최근 검색",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { onDeleteAllRecentMealSearches() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "모두 삭제하기",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 1.dp),
                color = Color.Black
            )
            LazyColumn {
                items(searches, key = { it.id }) { search ->
                    RecentSearchItem(
                        search = search,
                        onClick = { onItemClick(search.name) },
                        onDelete = { onItemDelete(search) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSearchItem(
    search: RecentMealSearch,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "최근 검색 아이콘",
            tint = Color.Gray,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = search.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "기록 삭제",
                tint = Color.Gray
            )
        }
    }
}
