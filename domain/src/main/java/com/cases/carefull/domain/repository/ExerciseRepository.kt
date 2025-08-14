package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseState
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.util.DataResult

interface ExerciseRepository {
	fun angleAnalyze(pose: Pose): ExerciseState
	suspend fun analyzeImage(image: Any): Result<Pose>
	suspend fun getAllExercise(userId:String): DataResult<List<ExerciseCollection>>
	suspend fun getDailyExerciseList(sports: String): DataResult<List<ExerciseType>>
	suspend fun addExerciseRecord(exerciseRecord: ExerciseCollection): DataResult<Unit>
	suspend fun resetExercise(exercise: ExerciseCollection): DataResult<Unit>
}