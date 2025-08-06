package com.cases.carefull.data.repository

import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.model.DietItemDto
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.domain.model.DietInfo
import com.cases.carefull.domain.model.MealType
import com.cases.carefull.domain.repository.DietRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DietRepositoryImpl(
	private val apiService: DietApiService,
	private val dietApiKey: String
) : DietRepository {
	
	override fun getAllMeals(): Flow<List<DietInfo>> {
		return fakeMealDatabase
	}
	
	override suspend fun addMeal(dietInfo: DietInfo) {
		fakeMealDatabase.update { currentList ->
			currentList + dietInfo
		}
	}
	
	override suspend fun removeMeal(dietInfo: DietInfo) {
		fakeMealDatabase.update { currentList ->
			currentList.filterNot { it.id == dietInfo.id }
		}
	}
	
	override suspend fun searchMeals(
		query: String
	) = runCatching {
		val response = apiService.getFoodList(
			apiKey = dietApiKey,
			foodName = query
		)
		val result = if (response.header.resultCode == "00") {
			val dtoList: List<DietItemDto> = response.body.items
			dtoList.map { it.toDomain() }
		} else {
			emptyList()
		}
		result
	}.getOrElse {
		emptyList()
	}
	
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
}
