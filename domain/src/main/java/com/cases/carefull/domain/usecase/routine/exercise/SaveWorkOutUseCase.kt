package com.cases.carefull.domain.usecase.routine.exercise

import com.cases.carefull.domain.model.routine.exercise.ExerciseCollection
import com.cases.carefull.domain.repository.routine.exercise.WorkOutRecordRepository
import com.cases.carefull.domain.util.DataResourceResult

class SaveWorkOutUseCase(
    private val repository: WorkOutRecordRepository
) {
    suspend operator fun invoke(record: ExerciseCollection): DataResourceResult<Boolean> {
        return repository.saveWorkOutCount(record)
    }
}
