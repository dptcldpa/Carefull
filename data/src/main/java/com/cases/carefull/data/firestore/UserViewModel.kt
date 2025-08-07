package com.cases.carefull.data.firestore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
	
	// 1. Firestore 인스턴스 가져오기
	private val db = Firebase.firestore
	
	// UI에 표시될 사용자 목록 상태
	private val _users = MutableStateFlow<List<User>>(emptyList())
	val users = _users.asStateFlow()
	
	// UI에 표시될 로딩/에러 상태
	private val _statusMessage = MutableStateFlow("")
	val statusMessage = _statusMessage.asStateFlow()
	
	init {
		// ViewModel이 생성될 때 모든 사용자를 불러옴
		fetchAllUsers()
	}
	
	// [쓰기] 새로운 사용자 추가하기
	fun addUser(name: String, email: String, age: Int) {
		viewModelScope.launch {
			_statusMessage.value = "사용자 추가 중..."
			val user = User(name, email, age)
			
			try {
				// 'users' 컬렉션에 새로운 문서를 추가합니다.
				// Firestore가 자동으로 고유한 ID를 생성해줍니다.
				db.collection("users")
					.add(user)
					.await() // 작업이 끝날 때까지 기다림
				
				_statusMessage.value = "사용자 추가 성공!"
				fetchAllUsers() // 목록 새로고침
				
			} catch (e: Exception) {
				_statusMessage.value = "에러: ${e.message}"
				Log.w("FirestoreTest", "Error adding document", e)
			}
		}
	}
	
	// [읽기] 모든 사용자 불러오기
	fun fetchAllUsers() {
		viewModelScope.launch {
			_statusMessage.value = "사용자 목록 로딩 중..."
			try {
				// 'users' 컬렉션의 모든 문서를 가져옵니다.
				val snapshot = db.collection("users").get().await()
				
				// Firestore 문서(DocumentSnapshot)를 User 객체 리스트로 변환
				val userList = snapshot.documents.mapNotNull { document ->
					document.toObject<User>()
				}
				
				_users.value = userList
				_statusMessage.value = "로딩 완료"
				
			} catch (e: Exception) {
				_statusMessage.value = "에러: ${e.message}"
				Log.w("FirestoreTest", "Error getting documents.", e)
			}
		}
	}
	
	// [읽기-조건] 특정 나이 이상의 사용자만 불러오기
	fun fetchUsersOlderThan(ageLimit: Int) {
		viewModelScope.launch {
			_statusMessage.value = "조건 검색 중..."
			try {
				val snapshot = db.collection("users")
					.whereGreaterThan("age", ageLimit) // 'age' 필드가 ageLimit보다 큰 문서
					.get()
					.await()
				
				val userList = snapshot.documents.mapNotNull { it.toObject<User>() }
				_users.value = userList
				_statusMessage.value = "조건 검색 완료"
				
			} catch (e: Exception) {
				_statusMessage.value = "에러: ${e.message}"
				Log.w("FirestoreTest", "Error getting documents with condition.", e)
			}
		}
	}
	
	// [실시간 업데이트] 데이터 변경을 실시간으로 감지하기
	fun listenForUserChanges() {
		// 'users' 컬렉션에 리스너를 추가합니다.
		// 데이터가 추가, 수정, 삭제될 때마다 이 코드가 자동으로 실행됩니다.
		db.collection("users")
			.addSnapshotListener { snapshot, e ->
				// 에러 발생 시
				if (e != null) {
					_statusMessage.value = "Listen failed: ${e.message}"
					Log.w("FirestoreTest", "Listen failed.", e)
					return@addSnapshotListener
				}
				
				// snapshot이 null이 아니면 (데이터가 있으면)
				if (snapshot != null && !snapshot.isEmpty) {
					_statusMessage.value = "데이터 변경 감지!"
					val userList = snapshot.documents.mapNotNull { it.toObject<User>() }
					_users.value = userList
				} else {
					_statusMessage.value = "데이터 없음"
				}
			}
		// 주의: 이 리스너는 ViewModel이 파괴될 때 자동으로 제거되지 않으므로,
		// 실제 앱에서는 onCleared()에서 리스너를 제거하는 로직이 필요합니다.
	}
}