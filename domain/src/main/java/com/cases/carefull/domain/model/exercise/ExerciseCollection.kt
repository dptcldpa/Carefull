package com.cases.carefull.domain.model.exercise

data class ExerciseCollection(
	val userId: String = "test",
	val exerciseType: ExerciseType,
	val count: Int = 0,
	val createdAt: Long = 0,
	val updatedAt: Long = 0,
	val weeklyCounts: Map<String, Int> = emptyMap(),
	val dailyCounts: Map<String, Int> = emptyMap()
)