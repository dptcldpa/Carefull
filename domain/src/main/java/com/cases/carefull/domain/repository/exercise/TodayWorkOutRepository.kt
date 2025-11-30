package com.cases.carefull.domain.repository.exercise

import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface TodayWorkOutRepository {
    fun fetchTodayWorkOut(): Flow<DataResourceResult<ExerciseType>>
    suspend fun getTodayWorkOutCount(
        userId: String,
        exerciseType: ExerciseType
    ): DataResourceResult<Int>
}
