package com.cases.carefull.domain.usecase.routine.exercise

import com.cases.carefull.domain.model.routine.exercise.ExerciseAnalyzer
import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.domain.model.routine.exercise.workOutAnalyzer.DumbbellCurlAnalyzer
import com.cases.carefull.domain.model.routine.exercise.workOutAnalyzer.DumbbellShoulderPressAnalyzer
import com.cases.carefull.domain.model.routine.exercise.workOutAnalyzer.PushUpAnalyzer
import com.cases.carefull.domain.model.routine.exercise.workOutAnalyzer.SquatAnalyzer

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
