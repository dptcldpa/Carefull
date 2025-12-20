package com.cases.carefull.domain.model.routine.exercise.workOutAnalyzer

import com.cases.carefull.domain.model.routine.exercise.ExerciseAnalyzer
import com.cases.carefull.domain.model.routine.exercise.ExerciseState
import com.cases.carefull.domain.model.routine.exercise.Pose
import com.cases.carefull.domain.model.routine.exercise.PoseLandmark
import com.cases.carefull.domain.model.routine.exercise.getAngle


class SquatAnalyzer: ExerciseAnalyzer {
	override fun analyze(pose: Pose): ExerciseState {
		val hip = pose.landmarks[PoseLandmark.LEFT_HIP]
		val knee = pose.landmarks[PoseLandmark.LEFT_KNEE]
		val ankle = pose.landmarks[PoseLandmark.LEFT_ANKLE]
		
		val kneeAngle = getAngle(hip, knee, ankle)
		
		return when {
			kneeAngle > 150 -> ExerciseState.UP
			kneeAngle < 90 -> ExerciseState.DOWN
			else -> ExerciseState.NONE
		}
	}
}
