package com.cases.carefull.domain.usecase.routine.exercise

import com.cases.carefull.domain.model.routine.exercise.ExerciseCollection
import com.cases.carefull.domain.repository.routine.exercise.WorkOutRecordRepository
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow

class GetWorkOutListUseCase (
    private val repository: WorkOutRecordRepository
) {
    operator fun invoke(userId: String): Flow<DataResourceResult<List<ExerciseCollection>>> {
        return repository.fetchWorkOutStats(userId)
    }
}
