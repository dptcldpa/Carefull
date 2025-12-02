package com.cases.carefull.domain.repository.routine.exercise

import com.cases.carefull.domain.model.routine.exercise.ExerciseCollection
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface WorkOutRecordRepository {
	fun fetchWorkOutStats(userId:String): Flow<DataResourceResult<List<ExerciseCollection>>>
	suspend fun saveWorkOutCount(workOutStats: ExerciseCollection): DataResourceResult<Boolean>
}
