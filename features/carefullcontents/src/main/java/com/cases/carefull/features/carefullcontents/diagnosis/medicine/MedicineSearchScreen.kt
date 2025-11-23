package com.cases.carefull.features.carefullcontents.diagnosis.medicine

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cases.carefull.domain.model.MedicineItem
import com.cases.carefull.domain.model.diet.RecentMealSearch
import com.cases.carefull.features.carefullcommon.components.SearchBar

@Composable
fun MedicineSearchScreen(
    viewModel: MedicineViewModel = hiltViewModel(),
    onNavigateToMedicineInfo: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            modifier = Modifier,
            query = uiState.searchQuery,
            onQueryChange = { newQuery ->
                viewModel.onQueryChange(newQuery)
            },
            onSearch = { viewModel.addRecentSearch(uiState.searchQuery) },
            placeholder = "약 이름을 검색하세요",
            label = "약 검색",
        )

//		Row(
//			verticalAlignment = Alignment.CenterVertically,
//			modifier = Modifier
//				.fillMaxWidth()
//				.padding(horizontal = 8.dp)
//		) {
//			// 왼쪽 카메라 아이콘
//			IconButton(onClick = { /* TODO: 카메라 기능 */ }) {
//				Icon(
//					imageVector = Icons.Default.Info,
//					contentDescription = "카메라"
//				)
//			}
//
//			Spacer(modifier = Modifier.width(8.dp))
//		}
//
//			TextField(
//				value = uiState.searchQuery,
//				onValueChange = { newQuery ->
//					viewModel.onQueryChange(newQuery)
//				},
//				placeholder = { Text("약 이름을 입력하세요", color = Color.Gray) },
//				singleLine = true,
//				modifier = Modifier
//					.weight(1f)
//					.padding(horizontal = 8.dp)
//					.background(Color(0xFFF0F0F0), RoundedCornerShape(20.dp)),
//				colors = TextFieldDefaults.colors(
//					unfocusedContainerColor = Color.Transparent,
//					focusedContainerColor = Color.Transparent,
//					unfocusedIndicatorColor = Color.Transparent,
//					focusedIndicatorColor = Color.Transparent
//				)
//			)
//			IconButton(
//				onClick = {
//					viewModel.addRecentSearch(uiState.searchQuery)
//				}) {
//				Icon(
//					imageVector = Icons.Default.Search,
//					contentDescription = "검색",
//					tint = Color.Gray
//				)
//			}
//		}
//		Spacer(modifier = Modifier.height(24.dp))

        if (uiState.searchQuery.isEmpty()) {
            RecentSearchSection(
                recentSearches = uiState.recentSearches,
                onSearch = { searchTerm ->
                    viewModel.onQueryChange(searchTerm)
                },
                onRemove = viewModel::removeRecentSearch,
                onClearAll = viewModel::clearRecentSearches
            )
        } else {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("에러가 발생했습니다: ${uiState.errorMessage}", color = Color.Red)
                }
            } else {
                SearchResultSection(
                    searchResults = uiState.searchResult,
                    onItemClick = { medicineItem ->
                        viewModel.setSelectedItem(medicineItem)
                        onNavigateToMedicineInfo()
                    }
                )
            }
        }
    }
}

@Composable
private fun RecentSearchSection(
    recentSearches: List<String>,
    onSearch: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (recentSearches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("최근 검색 기록이 없습니다.", color = Color.Gray)
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "최근 검색",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { onClearAll() },
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


            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(recentSearches) { term ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSearch(term) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(term, modifier = Modifier.weight(1f), fontSize = 16.sp)
                        IconButton(onClick = { onRemove(term) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "삭제", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}


//	Column {
//		Row(
//			modifier = Modifier.fillMaxWidth(),
//			horizontalArrangement = Arrangement.SpaceBetween,
//			verticalAlignment = Alignment.CenterVertically
//		) {
//			Text("최근 검색어", style = MaterialTheme.typography.titleMedium)
//			if (recentSearches.isNotEmpty()) {
//				Text(
//					text = "전체 삭제",
//					color = Color.Gray,
//					fontSize = 14.sp,
//					modifier = Modifier.clickable(onClick = onClearAll)
//				)
//			}
//		}
//		Spacer(modifier = Modifier.height(16.dp))
//
//		if (recentSearches.isEmpty()) {
//			Text("검색 기록이 없습니다.", color = Color.Gray)
//		} else {
//			LazyColumn(modifier = Modifier.fillMaxWidth()) {
//				items(recentSearches) { term ->
//					Row(
//						modifier = Modifier
//							.fillMaxWidth()
//							.clickable { onSearch(term) }
//							.padding(vertical = 12.dp),
//						verticalAlignment = Alignment.CenterVertically
//					) {
//						Text(term, modifier = Modifier.weight(1f), fontSize = 16.sp)
//						IconButton(onClick = { onRemove(term) }, modifier = Modifier.size(24.dp)) {
//							Icon(Icons.Default.Close, contentDescription = "삭제", tint = Color.Gray)
//						}
//					}
//				}
//			}
//		}
//	}
//}

@Composable
private fun SearchResultSection(
    searchResults: List<MedicineItem>,
    onItemClick: (MedicineItem) -> Unit
) {
    if (searchResults.isNotEmpty()) {
        Text("검색 결과", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(searchResults) { item ->
                OutlinedCard(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onItemClick(item)
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.itemName ?: "정보 없음", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.efcyQesitm ?: "",
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        };
    } else {
        Text("검색 결과가 없습니다.", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun RecentMedicineSearchesSection(
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
                    RecentMedicineSearchItem(
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
private fun RecentMedicineSearchItem(
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
