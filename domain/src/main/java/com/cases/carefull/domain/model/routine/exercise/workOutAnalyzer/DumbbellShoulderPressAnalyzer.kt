package com.cases.carefull.domain.model.routine.exercise.workOutAnalyzer

import com.cases.carefull.domain.model.routine.exercise.ExerciseAnalyzer
import com.cases.carefull.domain.model.routine.exercise.ExerciseState
import com.cases.carefull.domain.model.routine.exercise.Pose
import com.cases.carefull.domain.model.routine.exercise.PoseLandmark
import com.cases.carefull.domain.model.routine.exercise.getAngle

class DumbbellShoulderPressAnalyzer(private val isLeftHand: Boolean) : ExerciseAnalyzer {
    override fun analyze(pose: Pose): ExerciseState {
        val shoulder = pose.landmarks[
            if (isLeftHand) PoseLandmark.LEFT_SHOULDER
            else PoseLandmark.RIGHT_SHOULDER]
        val elbow = pose.landmarks[
            if (isLeftHand) PoseLandmark.LEFT_ELBOW
            else PoseLandmark.RIGHT_ELBOW]
        val wrist = pose.landmarks[
            if (isLeftHand) PoseLandmark.LEFT_WRIST
            else PoseLandmark.RIGHT_WRIST]

        val elbowAngle = getAngle(shoulder, elbow, wrist)

        return when {
            elbowAngle > 150 -> ExerciseState.UP
            elbowAngle < 100 -> ExerciseState.DOWN
            else -> ExerciseState.NONE
        }
    }
}
