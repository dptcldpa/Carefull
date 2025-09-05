package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ExerciseRepository {
	
	fun getExerciseStatFlow(userId:String): Flow<List<ExerciseCollection>>
	
	fun getCompletedDailyExerciseDatesFlow(userId: String): Flow<Set<LocalDate>>
	suspend fun getExerciseStat(userId:String): List<ExerciseCollection>
	suspend fun getDailyExerciseList(): List<ExerciseType>
	suspend fun addExerciseRecord(exerciseRecord: ExerciseCollection): Boolean
	suspend fun getTodaysExerciseCount(userId: String, exerciseType: ExerciseType): Int
	suspend fun getCompletedDailyExerciseDates(userId: String): Set<LocalDate>
	suspend fun markDailyExerciseAsCompleted(userId: String, date: LocalDate)
}