package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.exercise.ExerciseState
import com.cases.carefull.domain.model.exercise.Pose

interface ExerciseAnalyzer {
	fun analyze(pose: Pose): ExerciseState
}