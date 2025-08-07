package com.cases.carefull.features.carefullcontents.firestore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cases.carefull.data.firestore.UserViewModel

@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
	val users by viewModel.users.collectAsState()
	val status by viewModel.statusMessage.collectAsState()
	
	Column(modifier = Modifier.padding(16.dp)) {
		Text("Firestore 테스트", style = MaterialTheme.typography.headlineMedium)
		
		// 상태 메시지 표시
		Text(status, color = MaterialTheme.colorScheme.primary)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		// 사용자 추가 버튼
		Row {
			Button(onClick = { viewModel.addUser("새 사용자", "new@test.com", 25) }) {
				Text("25세 사용자 추가")
			}
			Spacer(modifier = Modifier.width(8.dp))
			Button(onClick = { viewModel.fetchUsersOlderThan(30) }) {
				Text("30세 이상 검색")
			}
		}
		
		Spacer(modifier = Modifier.height(16.dp))
		
		// 사용자 목록 표시
		LazyColumn {
			items(users) { user ->
				Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text("이름: ${user.name}", fontWeight = FontWeight.Bold)
						Text("이메일: ${user.email}")
						Text("나이: ${user.age}")
					}
				}
			}
		}
	}
}