package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.DietCollection
import com.cases.carefull.domain.model.DietInfo
import kotlinx.coroutines.flow.Flow

// 식단 데이터에 대한 규칙(계약서)
interface DietRepository {
	// 모든 식사 기록을 실시간으로 관찰할 수 있는 흐름(Flow)을 제공한다.
	fun getAllMeals(): Flow<List<DietInfo>>
	// 새로운 식사 기록을 추가한다.
	suspend fun addMeal(dietInfo: DietInfo)
	// 특정 식사 기록을 삭제한다.
	suspend fun removeMeal(dietInfo: DietInfo)
	
	suspend fun searchMeals(query: String): List<DietCollection>
	
	//firestore
	suspend fun getAllMealsFromFirestore(): DataResult<List<DietCollection>>
	suspend fun addMealToFirestore(mealData: DietCollection): DataResult<Unit>
//	suspend fun searchMealsTwo(query: String): List<DietCollection>
	suspend fun removeMealFromFirestore(mealData: DietCollection)
}

sealed interface DataResult<out T> {
	data class Success<T>(val data: T) : DataResult<T>
	data class Error(val exception: Throwable) : DataResult<Nothing>
	object Loading : DataResult<Nothing>
}