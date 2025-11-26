package com.cases.carefull.features.carefullcontents.diagnosis.disease

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cases.carefull.domain.model.Disease
import com.cases.carefull.features.carefullcommon.components.SearchBar

@Composable
fun DiseaseSearchScreen(
    viewModel: DiseaseViewModel = hiltViewModel(),
    onDiseaseClick: (String) -> Unit
) {
    val diseaseListState by viewModel.diseaseListState.collectAsStateWithLifecycle()
    val diseaseDetailState by viewModel.diseaseDetailState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val recentSearches = remember { mutableStateListOf<String>() }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            modifier = Modifier,
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            onSearch = {
                val input = searchQuery.trim()

                if (!recentSearches.contains(input)) {
                    recentSearches.add(0, input)
                    if (recentSearches.size > 10) recentSearches.removeAt(recentSearches.lastIndex)
                }

                viewModel.updateSearchQuery(input)
            },
            placeholder = "무엇이든 물어보세요",
        )
//         검색 입력창
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
//                .padding(horizontal = 16.dp, vertical = 8.dp)
//                .fillMaxWidth()
//        ) {
//            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "카메라")
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            TextField(
//                value = query,
//                onValueChange = { query = it },
//                placeholder = { Text("무엇이든 물어보세요") },
//                singleLine = true,
//                modifier = Modifier.weight(1f),
//                colors = TextFieldDefaults.colors(
//                    unfocusedIndicatorColor = Color.Transparent,
//                    focusedIndicatorColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent,
//                    errorIndicatorColor = Color.Transparent
//                )
//            )
//
//            if (query.text.isNotEmpty()) {
//                IconButton(onClick = { query = TextFieldValue("") }) {
//                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "지우기")
//                }
//            }
//
//            IconButton(onClick = {
//                val input = query.text.trim()
//                if (input.isEmpty()) {
//                    Toast.makeText(context, "검색어를 입력하세요", Toast.LENGTH_SHORT).show()
//                    return@IconButton
//                }
//                if (!recentSearches.contains(input)) {
//                    recentSearches.add(0, input)
//                    if (recentSearches.size > 10) recentSearches.removeAt(recentSearches.lastIndex)
//                }
//                searchResult = sampleDiseaseData.find { it.name == input }
//                if (searchResult == null) {
//                    Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }) {
//                Icon(imageVector = Icons.Default.Search, contentDescription = "검색")
//            }
//        }

        Spacer(modifier = Modifier.height(24.dp))

        // 최근 검색어
        RecentSearchSection(
            recentSearches = recentSearches,
            onSearchClick = { query ->
                viewModel.updateSearchQuery(query)
            },
            onDeleteClick = { query ->
                recentSearches.remove(query)
            },
            onClearAll = {
                recentSearches.clear()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            diseaseListState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            diseaseListState.errorMessage != null -> {
                Text(
                    text = diseaseListState.errorMessage ?: "오류 발생",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            diseaseListState.diseases.isEmpty() -> {
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "검색 결과가 없습니다.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            else -> {
                LazyColumn {
                    items(diseaseListState.diseases) { disease ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onDiseaseClick(disease.contentSn)
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = disease.diseaseName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
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
fun RecentSearchSection(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("최근 검색어", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "전체 삭제",
            color = Color.Gray,
            modifier = Modifier.clickable { onClearAll() }
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (recentSearches.isEmpty()) {
        Text("검색 기록이 없습니다.", color = Color.Gray)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 160.dp)
        ) {
            items(recentSearches) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onSearchClick(item) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item)
                    IconButton(onClick = { onDeleteClick(item) }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "삭제")
                    }
                }
            }
        }
    }
}

@Composable
fun DiseaseSearchResults(
    diseases: List<Disease>,
    onDiseaseClick: (Disease) -> Unit
) {
    LazyColumn {
        items(diseases) { disease ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onDiseaseClick(disease) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = disease.diseaseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    
                    disease.contentList.firstOrNull()?.let { content ->
                        Text(
                            text = content.content.take(100) + "...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}