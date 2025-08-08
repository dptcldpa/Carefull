package com.cases.carefull.data.repository

import com.cases.carefull.data.model.DietCollectionDTO
import com.cases.carefull.data.model.toDomainDietCollectionList
import com.cases.carefull.data.model.toFirestoreDietCollectionDTO
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.model.DietItemDto
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.domain.model.DietCollection
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.util.DataResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class DietRepositoryImpl(
	private val apiService: DietApiService,
	private val dietApiKey: String
) : DietRepository {
	
	private val db = Firebase.firestore
	
	override suspend fun getAllMeal(): DataResult<List<DietCollection>> =
		runCatching {
			val snapshot = db.collection("diet_collection").get().await()
			val dtoList = snapshot.toObjects(DietCollectionDTO::class.java)
			
			dtoList.toDomainDietCollectionList()
		}.map { dietList ->
			DataResult.Success(dietList)
			
		}.getOrElse { exception ->
			DataResult.Error(exception)
		}
	
	override suspend fun addMeal(mealData: DietCollection): DataResult<Unit> {
		return try {
			val dto = mealData.toFirestoreDietCollectionDTO()
			db.collection("diet_collection").add(dto).await()
			DataResult.Success(Unit)
		} catch (e: Exception) {
			DataResult.Error(e)
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
			val dtoList: List<DietItemDto> = response.body.items
			dtoList.map { it.toDomain() }
		} else {
			emptyList()
		}
	}.getOrElse {
		emptyList()
	}

	override suspend fun removeMeal(mealData: DietCollection) {
		TODO("Not yet implemented")
	}
}