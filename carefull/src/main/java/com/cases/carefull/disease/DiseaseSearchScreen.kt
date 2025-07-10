package com.openstudy.carefull.disease

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openstudy.carefull.chatbot.ChatBotScreen
import com.openstudy.carefull.ui.theme.CarefullTheme

private val sampleDiseaseData = listOf(
    DiseaseInfo("고혈압", "고혈압은 혈압이 높은 상태입니다."),
    DiseaseInfo("당뇨병", "당뇨병은 혈당이 비정상적으로 높은 상태입니다."),
    DiseaseInfo("감기", "감기는 바이러스에 의한 상기도 감염입니다.")
)

@Composable
fun DiseaseSearchScreen() {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var searchResult by remember { mutableStateOf<DiseaseInfo?>(null) }
    val recentSearches = remember { mutableStateListOf<String>() }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // 검색 입력창
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "카메라")

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("무엇이든 물어보세요") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

            if (query.text.isNotEmpty()) {
                IconButton(onClick = { query = TextFieldValue("") }) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "지우기")
                }
            }

            IconButton(onClick = {
                val input = query.text.trim()
                if (input.isEmpty()) {
                    Toast.makeText(context, "검색어를 입력하세요", Toast.LENGTH_SHORT).show()
                    return@IconButton
                }
                if (!recentSearches.contains(input)) {
                    recentSearches.add(0, input)
                    if (recentSearches.size > 10) recentSearches.removeAt(recentSearches.lastIndex)
                }
                searchResult = sampleDiseaseData.find { it.name == input }
                if (searchResult == null) {
                    Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "검색")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 최근 검색어
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("최근 검색어", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "전체 삭제",
                color = Color.Gray,
                modifier = Modifier.clickable {
                    recentSearches.clear()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (recentSearches.isEmpty()) {
            Text("검색 기록이 없습니다.", color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 160.dp)) {
                items(recentSearches) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                query = TextFieldValue(item)
                                searchResult = sampleDiseaseData.find { it.name == item }
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item)
                        IconButton(onClick = { recentSearches.remove(item) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "삭제")
                        }
                    }
                    Divider()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 검색 결과
        searchResult?.let {
            Text("검색 결과:", style = MaterialTheme.typography.titleMedium)
            Text("질병명: ${it.name}", style = MaterialTheme.typography.bodyLarge)
            Text("설명: ${it.description}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiseaseSearchScreenPreview() {
    CarefullTheme {
        DiseaseSearchScreen()
    }
}