package com.cases.carefull.data.repository

import android.util.Log
import com.cases.carefull.data.firestore.DietCollectionDTO
import com.cases.carefull.data.firestore.toDomainDietCollectionList
import com.cases.carefull.data.firestore.toFirestoreDietCollectionDTO
import com.cases.carefull.data.mapper.toDomainTwo
import com.cases.carefull.data.model.DietItemDtoTwo
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.domain.model.DietCollection
import com.cases.carefull.domain.model.DietInfo
import com.cases.carefull.domain.model.MealType
import com.cases.carefull.domain.repository.DataResult
import com.cases.carefull.domain.repository.DietRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class DietRepositoryImpl(
	private val apiService: DietApiService,
	private val dietApiKey: String
) : DietRepository {
	
	private val db = Firebase.firestore
	
	override fun getAllMeals(): Flow<List<DietInfo>> {
		return fakeMealDatabase
	}
	
	override suspend fun addMeal(dietInfo: DietInfo) {
		fakeMealDatabase.update { currentList ->
			currentList + dietInfo
		}
	}
	
	
	override suspend fun getAllMealsFromFirestore(): DataResult<List<DietCollection>> =
		runCatching {
			val snapshot = db.collection("diet_collection").get().await()
			val dtoList = snapshot.toObjects(DietCollectionDTO::class.java)
			
			dtoList.toDomainDietCollectionList()
		}.map { dietList ->
			// --- runCatching 성공 시 ---
			// 4. 성공적으로 변환된 dietList를 DataResult.Success로 감싸줍니다.
			DataResult.Success(dietList)
			
		}.getOrElse { exception ->
			// --- runCatching 실패 시 ---
			// 5. 발생한 예외를 Log로 남기고 DataResult.Error로 감싸서 반환합니다.
			Log.e("FirestoreError", "Error in getAllMealsFromFirestore", exception)
			DataResult.Error(exception)
		}
	
	override suspend fun addMealToFirestore(mealData: DietCollection): DataResult<Unit> {
		return try {
			val dto = mealData.toFirestoreDietCollectionDTO()
			Log.d("MEAL_TYPE_TEST", "Repository: DTO로 변환된 meal_type: ${dto.meal_type}")
			db.collection("diet_collection").add(dto).await()
			DataResult.Success(Unit)
		} catch (e: Exception) {
			DataResult.Error(e)
		}
	}
	
	
	override suspend fun removeMeal(dietInfo: DietInfo) {
		fakeMealDatabase.update { currentList ->
			currentList.filterNot { it.id == dietInfo.id }
		}
	}
	
	override suspend fun searchMeals(
		query: String
	):List<DietCollection> = runCatching {
		val response = apiService.getFoodList(
			apiKey = dietApiKey,
			foodName = query
		)
		if (response.header.resultCode == "00" && response.body.items.isNotEmpty()) {
			val dtoList: List<DietItemDtoTwo> = response.body.items
//		val result = if (response.header.resultCode == "00") {
//			val dtoList: List<DietItemDtoTwo> = response.body.items
			dtoList.map { it.toDomainTwo() }
		} else {
			emptyList()
		}
	}.getOrElse {
		emptyList()
	}
	
//	override suspend fun searchMealsTwo(
//		query: String
//	) = runCatching {
//		val response = apiService.getFoodList(
//			apiKey = dietApiKey,
//			foodName = query
//		)
//		val result = if (response.header.resultCode == "00") {
//			val dtoList: List<DietItemDtoTwo> = response.body.items
//			dtoList.map { it.toDomain() }
//		} else {
//			emptyList()
//		}
//		result
//	}.getOrElse {
//		emptyList()
//	}
//
//		return try {
//			// Firestore에서 'mealName'이 query로 시작하는 문서를 검색합니다.
//			// \uf8ff는 유니코드에서 매우 큰 값 중 하나로, 'query'로 시작하는 모든 문서를
//			// 포함하기 위한 트릭입니다. (예: "새우"로 시작하는 "새우깡", "새우버거" 등을 모두 찾음)
//			val snapshot = db.collection("diet_collection")
//				.whereGreaterThanOrEqualTo("mealName", query)
//				.whereLessThanOrEqualTo("mealName", query + "\uf8ff")
//				.get()
//				.await()
//
//			// DTO로 변환
//			val dtoList = snapshot.toObjects(DietCollectionDTO::class.java)
//
//			// Domain 모델로 변환하여 반환
//			dtoList.toDomainDietCollectionList()
//
//		} catch (e: Exception) {
//			Log.e("FirestoreSearchError", "Error searching meals for query: $query", e)
//			// 검색 실패 시 빈 리스트 반환
//			emptyList()
//		}
//	}
	
	override suspend fun removeMealFromFirestore(mealData: DietCollection) {
		TODO("Not yet implemented")
	}
}


//	override suspend fun searchMealsFromFirestore(
//		query: String
//		) = runCatching {
//			val response = apiService.getFoodList(
//				apiKey = dietApiKey,
//				foodName = query
//			)
//			val result = if (response.header.resultCode == "00") {
//				val dtoList: List<DietItemDtoTwo> = response.body.items
//				dtoList.map { it.toDomain() }
//			} else {
//				emptyList()
//			}
//			result
//		}.getOrElse {
//			emptyList()
//		}

// 더미데이터
private val fakeMealDatabase = MutableStateFlow(
	listOf(
		DietInfo(1L, "삶은 계란", 155, 100, 100, 100, 100, MealType.BREAKFAST),
		DietInfo(2L, "아몬드", 100, 155, 100, 100, 100, MealType.SNACK),
		DietInfo(3L, "우유", 50, 155, 100, 100, 100, MealType.LUNCH),
		DietInfo(4L, "바나나", 100, 155, 100, 100, 100, MealType.DINNER),
		DietInfo(5L, "김치", 50, 100, 100, 100, 100, MealType.SNACK),
		DietInfo(6L, "라면", 100, 155, 100, 100, 100, MealType.BREAKFAST),
		DietInfo(7L, "피자", 200, 155, 100, 100, 100, MealType.LUNCH),
		DietInfo(8L, "햄버거", 150, 155, 100, 100, 100, MealType.DINNER)
	)
)
