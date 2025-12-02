package com.cases.carefull.data.mapper

import com.cases.carefull.domain.model.routine.exercise.Landmark
import com.cases.carefull.domain.model.routine.exercise.Pose
import com.cases.carefull.domain.model.routine.exercise.Position
import com.google.mlkit.vision.pose.Pose as MlKitPose

fun MlKitPose.toDomainPose(): Pose {
	val landmarks = this.allPoseLandmarks.associate { mlKitLandmark ->
		mlKitLandmark.landmarkType to Landmark(
			type = mlKitLandmark.landmarkType,
			position = Position(mlKitLandmark.position.x, mlKitLandmark.position.y),
			inFrameLikelihood = mlKitLandmark.inFrameLikelihood
		)
	}
	return Pose(landmarks)
}