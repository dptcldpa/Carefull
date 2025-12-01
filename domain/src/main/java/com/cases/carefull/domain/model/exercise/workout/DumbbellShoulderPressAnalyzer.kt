package com.cases.carefull.domain.model.exercise.workout

import com.cases.carefull.domain.model.exercise.ExerciseAnalyzer
import com.cases.carefull.domain.model.exercise.ExerciseState
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.model.exercise.getAngle

class DumbbellShoulderPressAnalyzer(private val isLeftHand: Boolean) : ExerciseAnalyzer {
    override fun analyze(pose: Pose): ExerciseState {
        val shoulder = pose.landmarks[if (isLeftHand) 11 else 12]
        val elbow = pose.landmarks[if (isLeftHand) 13 else 14]
        val wrist = pose.landmarks[if (isLeftHand) 15 else 16]

        val elbowAngle = getAngle(shoulder, elbow, wrist)

        return when {
            elbowAngle > 150 -> ExerciseState.UP
            elbowAngle < 100 -> ExerciseState.DOWN
            else -> ExerciseState.NONE
        }
    }
}