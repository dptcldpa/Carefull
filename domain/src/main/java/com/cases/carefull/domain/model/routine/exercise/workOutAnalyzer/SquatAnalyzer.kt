package com.cases.carefull.domain.model.routine.exercise.workOutAnalyzer

import com.cases.carefull.domain.model.routine.exercise.ExerciseAnalyzer
import com.cases.carefull.domain.model.routine.exercise.ExerciseState
import com.cases.carefull.domain.model.routine.exercise.Pose
import com.cases.carefull.domain.model.routine.exercise.getAngle

class SquatAnalyzer: ExerciseAnalyzer {
	override fun analyze(pose: Pose): ExerciseState {
		val hip = pose.landmarks[24]
		val knee = pose.landmarks[26]
		val ankle = pose.landmarks[28]
		
		val kneeAngle = getAngle(hip, knee, ankle)
		
		return when {
			kneeAngle > 150 -> ExerciseState.UP
			kneeAngle < 90 -> ExerciseState.DOWN
			else -> ExerciseState.NONE
		}
	}
}