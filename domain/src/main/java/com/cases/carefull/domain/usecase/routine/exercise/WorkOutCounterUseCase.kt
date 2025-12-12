package com.cases.carefull.domain.usecase.routine.exercise

import com.cases.carefull.domain.model.routine.exercise.ExerciseState
import com.cases.carefull.domain.model.routine.exercise.RepetitionResult

class WorkOutCounterUseCase {
    operator fun invoke(
        currentCount: Int,
        lastConfirmedPose: ExerciseState,
        newDetectedPose: ExerciseState
    ): RepetitionResult {
        if (newDetectedPose != ExerciseState.UP && newDetectedPose != ExerciseState.DOWN) {
            return RepetitionResult(
                count = currentCount,
                currentPose = lastConfirmedPose,
                isRepetitionCompleted = false
            )
        }
        val isRepetitionFinished =
            (lastConfirmedPose == ExerciseState.DOWN && newDetectedPose == ExerciseState.UP)
        val newCount = if (isRepetitionFinished) currentCount + 1 else currentCount
        return RepetitionResult(
            count = newCount,
            currentPose = newDetectedPose,
            isRepetitionCompleted = isRepetitionFinished
        )
    }
}
