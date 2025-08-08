package com.cases.carefull.data.model

import com.cases.carefull.domain.model.DietCollection
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class DietCollectionDTO(
	val user_id: String = "",
	val meal_type: String = "",
	val meal_name: String = "",
	val kcal: Int = 0,
	val carbohydrate: Int = 0,
	val protein: Int = 0,
	val fat: Int = 0,
	val weight: Int = 0,
	@ServerTimestamp
	val created_at: Timestamp? = null,
	@ServerTimestamp
	val updated_at: Timestamp? = null
)

fun DietCollection.toFirestoreDietCollectionDTO(): DietCollectionDTO {
	return DietCollectionDTO(
		user_id = this.userId,
		meal_type = this.mealType,
		meal_name = this.mealName,
		kcal = this.kcal,
		carbohydrate = this.carbohydrate,
		protein = this.protein,
		fat = this.fat,
		weight = this.weight,
		created_at = null,
		updated_at = null
	)
}

fun DietCollectionDTO.toDomainDietCollection(): DietCollection {
	return DietCollection(
		userId = this.user_id,
		mealType = this.meal_type,
		mealName = this.meal_name,
		kcal = this.kcal,
		carbohydrate = this.carbohydrate,
		protein = this.protein,
		fat = this.fat,
		weight = this.weight,
		createdAt = this.created_at?.toDate()?.time ?: 0L,
		updatedAt = this.updated_at?.toDate()?.time ?: 0L
	)
}

fun List<DietCollectionDTO>.toDomainDietCollectionList() =
	this.map { it.toDomainDietCollection() }.toList()