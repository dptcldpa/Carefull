package com.cases.carefull.data.model

import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ExerciseCollectionDTO(
	val user_id: String = "",
	val category_id: String = "",
	val count: Int = 0,
	@ServerTimestamp
	val created_at: Timestamp? = null,
	@ServerTimestamp
	val updated_at: Timestamp? = null,
	val weekly_counts: Map<String, Int> = emptyMap(),
	val daily_counts:Map<String,Int> = emptyMap()
)

fun ExerciseCollection.toFirestoreExerciseCollectionDTO(): ExerciseCollectionDTO {
	return ExerciseCollectionDTO(
		user_id = this.userId,
		category_id = this.exerciseType.name,
		count = this.count,
		created_at = null,
		updated_at = null,
		weekly_counts = this.weeklyCounts,
		daily_counts = this.dailyCounts
	)
}

fun ExerciseCollectionDTO.toDomainExerciseCollection(): ExerciseCollection {
	return ExerciseCollection(
		userId = this.user_id,
		exerciseType = ExerciseType.entries.find { it.name == this.category_id } ?: ExerciseType.SQUAT,
		count = this.count,
		createdAt = this.created_at?.toDate()?.time ?: 0L,
		updatedAt = this.updated_at?.toDate()?.time ?: 0L,
		weeklyCounts = this.weekly_counts,
		dailyCounts = this.daily_counts
	)
}

fun List<ExerciseCollectionDTO>.toDomainExerciseCollectionList() =
	this.map { it.toDomainExerciseCollection() }.toList()