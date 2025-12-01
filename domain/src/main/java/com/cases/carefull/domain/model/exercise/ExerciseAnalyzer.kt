package com.cases.carefull.domain.model.exercise

interface ExerciseAnalyzer {
    fun analyze(pose: Pose): ExerciseState
}
