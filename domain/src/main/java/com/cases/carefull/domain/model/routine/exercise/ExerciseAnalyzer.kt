package com.cases.carefull.domain.model.routine.exercise

interface ExerciseAnalyzer {
    fun analyze(pose: Pose): ExerciseState
}
