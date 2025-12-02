package com.cases.carefull.domain.repository.routine.exercise

import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

interface TodayWorkOutRepository {
    fun fetchTodayWorkOut(): Flow<DataResourceResult<ExerciseType>>
    fun getTodayWorkOutCount(
        userId: String,
        exerciseType: ExerciseType
    ): Flow<DataResourceResult<Int>>
}
