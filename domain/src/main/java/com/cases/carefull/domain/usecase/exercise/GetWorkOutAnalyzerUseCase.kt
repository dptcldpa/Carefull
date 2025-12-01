package com.cases.carefull.domain.usecase.exercise

import com.cases.carefull.domain.model.exercise.ExerciseAnalyzer
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.model.exercise.workout.DumbbellCurlAnalyzer
import com.cases.carefull.domain.model.exercise.workout.DumbbellShoulderPressAnalyzer
import com.cases.carefull.domain.model.exercise.workout.PushUpAnalyzer
import com.cases.carefull.domain.model.exercise.workout.SquatAnalyzer

class GetWorkOutAnalyzerUseCase {
    operator fun invoke(type: ExerciseType): ExerciseAnalyzer {
        return when (type) {
            ExerciseType.DUMBBELL_CURL -> DumbbellCurlAnalyzer(isLeftHand = false)
            ExerciseType.SQUAT -> SquatAnalyzer()
            ExerciseType.PUSH_UP -> PushUpAnalyzer(isLeftHand = false)
            ExerciseType.DUMBBELL_SHOULDER_PRESS -> DumbbellShoulderPressAnalyzer(isLeftHand = false)
        }
    }
}
