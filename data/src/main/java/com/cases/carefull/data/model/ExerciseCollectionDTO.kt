package com.cases.carefull.data.model

import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ExerciseCollectionDTO(
	val user_id: String = "",
	val category_id: String = "",
	val count: Int = 0,
	@ServerTimestamp
	val created_at: Timestamp? = null,
	@ServerTimestamp
	val updated_at: Timestamp? = null
)

fun ExerciseCollection.toFirestoreExerciseCollectionDTO(): ExerciseCollectionDTO {
	return ExerciseCollectionDTO(
		user_id = this.userId,
		category_id = this.exerciseType,
		count = this.count,
		created_at = null,
		updated_at = null
	)
}

fun ExerciseCollectionDTO.toDomainExerciseCollection(): ExerciseCollection {
	return ExerciseCollection(
		userId = this.user_id,
		exerciseType = this.category_id,
		count = this.count,
		createdAt = this.created_at?.toDate()?.time ?: 0L,
		updatedAt = this.updated_at?.toDate()?.time ?: 0L
	)
}

fun List<ExerciseCollectionDTO>.toDomainExerciseCollectionList() =
	this.map { it.toDomainExerciseCollection() }.toList()