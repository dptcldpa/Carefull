package com.cases.carefull.features.carefullcontents.routine.diet

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.cases.carefull.domain.model.diet.FavoriteFood
import com.cases.carefull.domain.model.diet.FoodDataInputType
import com.cases.carefull.domain.model.diet.FoodItem
import com.cases.carefull.domain.model.diet.RecentFoodSearch
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.components.CommonAlertDialog
import com.cases.carefull.features.carefullcommon.components.CommonNumberOutLinedTextField
import com.cases.carefull.features.carefullcommon.components.CommonTextOutLinedTextField
import com.cases.carefull.features.carefullcommon.components.SearchBar
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
fun DietSearchRoute(
    viewModel: DietViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val route = navController.currentBackStackEntry?.toRoute<RoutineRoute.DietSearchRoute>()
    val mealType = route?.mealType
    val selectedDate = remember(route?.date) {
        route?.date?.let { LocalDate.parse(it) }
    }
    if (mealType == null || selectedDate == null) {
        return
    }
    LaunchedEffect(
        uiState.customInputState.carbohydrate,
        uiState.customInputState.protein,
        uiState.customInputState.fat
    ) {
        viewModel.calculateCustomInputKcal()
    }
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is NavigationEvent.NavigateBackToDietScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }
    DietSearchScreen(
        uiState = uiState,
        searchQuery = searchQuery,
        mealType = mealType,
        selectedDate = selectedDate,
        onSearchQueryChange = viewModel::onSearchQueryChanged,
        onSearch = { viewModel.onSearchFoods(searchQuery) },
        onAddFood = { food, mealType, selectedDate, servingSize ->
            viewModel.onAddFood(food, mealType, selectedDate, servingSize)
        },
        onRecentSearchClick = viewModel::onSearchFoods,
        onClearAllRecentSearches = viewModel::onClearAllRecentMealSearches,
        onDeleteRecentSearch = viewModel::onDeleteRecentSearch,
        onShowFavoritesDialog = viewModel::showFavoritesDialog,
        onHideFavoritesDialog = viewModel::hideFavoritesDialog,
        onFavoriteFoodClick = viewModel::onFavoriteFoodSelected,
        onFavoriteDelete = viewModel::onDeleteFavoriteFood,
        onFavoriteWeightConfirm = { weight ->
            viewModel.onAddFavoriteFood(weight, mealType, selectedDate)
        },
        onDismissEditFavoriteDialog = viewModel::hideEditServingSizeDialog,
        onShowCustomInputDialog = viewModel::showCustomInputDialog,
        onHideCustomInputDialog = viewModel::hideCustomInputDialog,
        onCustomInputChange = viewModel::onCustomInputChanged,
        onCustomInputFavoriteChange = viewModel::onCustomInputFavoriteChanged,
        onCustomInputConfirm = {
            viewModel.onAddCustomFood(selectedDate, mealType)
        }
    )
}

@Composable
fun DietSearchScreen(
    uiState: MainDietUiState,
    searchQuery: String,
    mealType: String,
    selectedDate: LocalDate,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onAddFood: (FoodItem, String, LocalDate, Int) -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onClearAllRecentSearches: () -> Unit,
    onDeleteRecentSearch: (RecentFoodSearch) -> Unit,
    onShowFavoritesDialog: () -> Unit,
    onHideFavoritesDialog: () -> Unit,
    onFavoriteFoodClick: (FavoriteFood) -> Unit,
    onFavoriteDelete: (FavoriteFood) -> Unit,
    onFavoriteWeightConfirm: (Int) -> Unit,
    onDismissEditFavoriteDialog: () -> Unit,
    onShowCustomInputDialog: () -> Unit,
    onHideCustomInputDialog: () -> Unit,
    onCustomInputChange: (FoodDataInputType, String) -> Unit,
    onCustomInputFavoriteChange: (Boolean) -> Unit,
    onCustomInputConfirm: () -> Unit
) {
    var foodToEdit by remember { mutableStateOf<FoodItem?>(null) }

    foodToEdit?.let { food ->
        EditWeightDialog(
            item = food,
            onConfirm = { newWeight ->
                onAddFood(food, mealType, selectedDate, newWeight)
                foodToEdit = null
            },
            onDismiss = {
                foodToEdit = null
            },
        )
    }
    if (uiState.favoriteState.isFavoritesDialogVisible) {
        FavoritesDialog(
            favorites = uiState.favoriteState.favoriteFoods,
            onDismiss = onHideFavoritesDialog,
            onSelect = onFavoriteFoodClick,
            onDelete = onFavoriteDelete
        )
    }
    uiState.favoriteState.selectedFavoriteForEditing?.let { favoriteMeal ->
        val foodItemForItem = FoodItem(
            name = favoriteMeal.name,
            servingSize = favoriteMeal.servingSize,
            kcal = favoriteMeal.kcal,
            carbohydrate = favoriteMeal.carbohydrate,
            protein = favoriteMeal.protein,
            fat = favoriteMeal.fat
        )
        EditWeightDialog(
            item = foodItemForItem,
            onConfirm =
                onFavoriteWeightConfirm,
            onDismiss = {
                onDismissEditFavoriteDialog
            }
        )
    }
    if (uiState.customInputState.isDialogVisible) {
        CustomInputAlertDialog(
            state = uiState.customInputState,
            onInputChange = onCustomInputChange,
            onFavoriteChanged = onCustomInputFavoriteChange,
            onConfirm = onCustomInputConfirm,
            onDismiss = onHideCustomInputDialog
        )
    }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                modifier = Modifier,
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = onSearch,
                placeholder = stringResource(R.string.food_search_hint),
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onShowFavoritesDialog,
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.common_favorites))
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = onShowCustomInputDialog,
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.food_manual_entry_button))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (uiState.dietSearchState.searchResults.isEmpty() && searchQuery.isBlank()) {
                        RecentMealSearchesSection(
                            searches = uiState.dietSearchState.recentSearches,
                            onDeleteAllRecentMealSearches = onClearAllRecentSearches,
                            onItemClick = onRecentSearchClick,
                            onItemDelete = onDeleteRecentSearch
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.dietSearchState.searchResults) { foodItem ->
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
    item: FoodItem,
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
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        stringResource(
                            R.string.weight_in_parentheses_format,
                            item.servingSize
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        stringResource(
                            R.string.kcal_format,
                            item.kcal
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(
                            id = R.string.nutrient_label_format,
                            stringResource(R.string.nutrient_carbohydrate),
                            item.carbohydrate,
                            stringResource(R.string.unit_gram)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(
                            id = R.string.nutrient_label_format,
                            stringResource(R.string.nutrient_protein),
                            item.protein,
                            stringResource(R.string.unit_gram)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(
                            id = R.string.nutrient_label_format,
                            stringResource(R.string.nutrient_fat),
                            item.fat,
                            stringResource(R.string.unit_gram)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun CustomInputAlertDialog(
    state: CustomInputState,
    onInputChange: (FoodDataInputType, String) -> Unit,
    onFavoriteChanged: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    CommonAlertDialog(
        onDismissRequest = onDismiss,
        title = { R.string.dialog_title_food_manual_entry },
        content = {
            LazyColumn {
                item {
                    CommonTextOutLinedTextField(
                        modifier = Modifier,
                        value = state.name,
                        onValueChange = { onInputChange(FoodDataInputType.NAME, it) },
                        label = { Text(stringResource(R.string.label_food_name)) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonNumberOutLinedTextField(
                        modifier = Modifier,
                        value = state.servingSize,
                        onValueChange = { onInputChange(FoodDataInputType.SERVING_SIZE, it) },
                        label = { Text(stringResource(R.string.label_weight_with_unit)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonNumberOutLinedTextField(
                        modifier = Modifier,
                        value = state.carbohydrate,
                        onValueChange = { onInputChange(FoodDataInputType.CARBOHYDRATE, it) },
                        label = { Text(stringResource(R.string.label_carbohydrate_with_unit)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonNumberOutLinedTextField(
                        modifier = Modifier,
                        value = state.protein,
                        onValueChange = { onInputChange(FoodDataInputType.PROTEIN, it) },
                        label = { Text(stringResource(R.string.label_protein_with_unit)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonNumberOutLinedTextField(
                        modifier = Modifier,
                        value = state.fat,
                        onValueChange = { onInputChange(FoodDataInputType.FAT, it) },
                        label = { Text(stringResource(R.string.label_fat_with_unit)) }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ResultRowComponent(
                        label = stringResource(R.string.label_total_calories),
                        value = state.calculatedKcal
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = state.name.isNotBlank() && state.servingSize.isNotBlank()
            ) {
                Text(stringResource(R.string.common_register))
            }
        },
        dismissButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = state.isFavorite,
                    onCheckedChange = onFavoriteChanged
                )
                Text(
                    stringResource(R.string.common_favorites),
                    modifier = Modifier.clickable { onFavoriteChanged(!state.isFavorite) })
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
            }
        }
    )
}

@Composable
fun EditWeightDialog(
    item: FoodItem,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var weight by remember { mutableStateOf(item.servingSize.toString()) }
    CommonAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(
                    R.string.dialog_title_edit_weight_for_meal,
                    item.name
                )
            )
        },
        content = {
            CommonNumberOutLinedTextField(
                modifier = Modifier,
                value = weight,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        weight = newValue
                    }
                },
                label = { Text(stringResource(R.string.label_new_weight_with_unit)) }
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
                Text(stringResource(R.string.common_confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}

@Composable
fun FavoritesDialog(
    favorites: List<FavoriteFood>,
    onDismiss: () -> Unit,
    onSelect: (FavoriteFood) -> Unit,
    onDelete: (FavoriteFood) -> Unit
) {
    CommonAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_title_favorites_list)) },
        content = {
            if (favorites.isEmpty()) {
                Text(stringResource(R.string.favorites_empty_message))
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
                Text(stringResource(R.string.common_close))
            }
        }
    )
}

@Composable
private fun FavoriteItemRow(
    meal: FavoriteFood,
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
                stringResource(
                    R.string.kcal_format,
                    meal.kcal
                ),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Row {
                Text(
                    stringResource(
                        id = R.string.nutrient_label_format,
                        stringResource(R.string.nutrient_carbohydrate_short),
                        meal.carbohydrate,
                        stringResource(R.string.unit_gram)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    stringResource(
                        id = R.string.nutrient_label_format,
                        stringResource(R.string.nutrient_protein_short),
                        meal.protein,
                        stringResource(R.string.unit_gram)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    stringResource(
                        id = R.string.nutrient_label_format,
                        stringResource(R.string.nutrient_fat_short),
                        meal.fat,
                        stringResource(R.string.unit_gram)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.common_delete))
        }
    }
}

@Composable
fun RecentMealSearchesSection(
    searches: List<RecentFoodSearch>,
    onDeleteAllRecentMealSearches: () -> Unit,
    onItemClick: (String) -> Unit,
    onItemDelete: (RecentFoodSearch) -> Unit
) {
    if (searches.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.search_history_empty_message), color = Color.Gray)
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
                    stringResource(R.string.search_history_title),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { onDeleteAllRecentMealSearches() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        stringResource(R.string.common_delete_all),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 1.dp),
                color = Color.Black
            )
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(searches, key = { it.query }) { search ->
                    RecentSearchItem(
                        search = search,
                        onClick = { onItemClick(search.query) },
                        onDelete = { onItemDelete(search) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSearchItem(
    search: RecentFoodSearch,
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
            contentDescription = stringResource(R.string.content_description_history_icon),
            tint = Color.Gray,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = search.query,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.content_description_delete_item),
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DietSearchScreenPreview_Search() {
    val date = LocalDate.now()
    val sampleFoodItems = listOf(
        FoodItem(
            name = "닭가슴살",
            servingSize = 100,
            kcal = 109,
            carbohydrate = 0,
            protein = 23,
            fat = 1
        ),
        FoodItem(
            name = "현미밥",
            servingSize = 210,
            kcal = 320,
            carbohydrate = 70,
            protein = 6,
            fat = 2
        ),
        FoodItem(
            name = "사과",
            servingSize = 200,
            kcal = 114,
            carbohydrate = 30,
            protein = 0,
            fat = 0
        )
    )
    val previewUiState = MainDietUiState(
        isLoading = false,
        dietSearchState = DietSearchState(
            searchResults = sampleFoodItems,
            recentSearches = emptyList()
        )
    )
    CarefullTheme {
        DietSearchScreen(
            uiState = previewUiState,
            searchQuery = "닭",
            mealType = "저녁",
            selectedDate = date,
            onSearchQueryChange = {},
            onSearch = {},
            onAddFood = { _, _, _, _ -> },
            onRecentSearchClick = {},
            onClearAllRecentSearches = {},
            onDeleteRecentSearch = {},
            onShowFavoritesDialog = {},
            onHideFavoritesDialog = {},
            onFavoriteFoodClick = {},
            onFavoriteDelete = {},
            onFavoriteWeightConfirm = {},
            onDismissEditFavoriteDialog = {},
            onShowCustomInputDialog = {},
            onHideCustomInputDialog = {},
            onCustomInputChange = { _, _ -> },
            onCustomInputFavoriteChange = {},
            onCustomInputConfirm = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DietSearchScreenPreview_Default() {
    val date = LocalDate.now()
    val time = System.currentTimeMillis()
    val sampleRecentSearches = listOf(
        RecentFoodSearch(query = "프로틴", searchedAt = time),
        RecentFoodSearch(query = "샐러드", searchedAt = time + 1)
    )
    val previewUiState = MainDietUiState(
        isLoading = false,
        dietSearchState = DietSearchState(
            searchResults = emptyList(),
            recentSearches = sampleRecentSearches
        )
    )
    CarefullTheme {
        DietSearchScreen(
            uiState = previewUiState,
            searchQuery = "",
            mealType = "저녁",
            selectedDate = date,
            onSearchQueryChange = {},
            onSearch = {},
            onAddFood = { _, _, _, _ -> },
            onRecentSearchClick = {},
            onClearAllRecentSearches = {},
            onDeleteRecentSearch = {},
            onShowFavoritesDialog = {},
            onHideFavoritesDialog = {},
            onFavoriteFoodClick = {},
            onFavoriteDelete = {},
            onFavoriteWeightConfirm = {},
            onDismissEditFavoriteDialog = {},
            onShowCustomInputDialog = {},
            onHideCustomInputDialog = {},
            onCustomInputChange = { _, _ -> },
            onCustomInputFavoriteChange = {},
            onCustomInputConfirm = { }
        )
    }
}
