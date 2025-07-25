package com.cases.carefull.features.carefullcontents.routine

import kotlinx.coroutines.flow.Flow

// 식단 데이터에 대한 규칙(계약서)
interface DietRepository {
	// 모든 식사 기록을 실시간으로 관찰할 수 있는 흐름(Flow)을 제공한다.
	fun getAllMeals(): Flow<List<MealRecord>>
	
	// 새로운 식사 기록을 추가한다.
	suspend fun addMeal(mealRecord: MealRecord)
	
	// 특정 식사 기록을 삭제한다.
	suspend fun removeMeal(mealRecord: MealRecord)
}