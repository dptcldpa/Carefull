package com.cases.carefull.domain.model.exercise.analyzer

import com.cases.carefull.domain.model.exercise.ExerciseState
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.model.exercise.getAngle
import com.cases.carefull.domain.repository.ExerciseAnalyzer

class SquatAnalyzer: ExerciseAnalyzer {
	override fun analyze(pose: Pose): ExerciseState {
		val hip = pose.landmarks[24]
		val knee = pose.landmarks[26]
		val ankle = pose.landmarks[28]
		
		val kneeAngle = getAngle(hip, knee, ankle)
		
		return when {
			kneeAngle > 160 -> ExerciseState.UP
			kneeAngle < 90 -> ExerciseState.DOWN
			else -> ExerciseState.NONE
		}
	}
}