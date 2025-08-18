package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.util.DataResult

interface ExerciseRepository {
	suspend fun getExerciseStat(userId:String): List<ExerciseCollection>
	suspend fun getDailyExerciseList(): List<ExerciseType>
	suspend fun addExerciseRecord(exerciseRecord: ExerciseCollection): Boolean
	suspend fun resetExercise(exercise: ExerciseCollection): DataResult<Unit>
}