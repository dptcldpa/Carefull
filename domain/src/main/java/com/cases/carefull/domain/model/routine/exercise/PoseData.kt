package com.cases.carefull.domain.model.routine.exercise

import kotlin.math.abs
import kotlin.math.atan2

data class Pose(
	val landmarks: Map<Int, Landmark>
)

data class Landmark(
	val type: Int,
	val position: Position,
	val inFrameLikelihood: Float
)

data class Position(
	val x: Float,
	val y: Float
)

const val MIN_CONFIDENCE = 0.7f

fun getAngle(firstPoint: Landmark?, midPoint: Landmark?, lastPoint: Landmark?): Double {
	if (firstPoint == null || midPoint == null || lastPoint == null ||
		firstPoint.inFrameLikelihood < MIN_CONFIDENCE ||
		midPoint.inFrameLikelihood < MIN_CONFIDENCE ||
		lastPoint.inFrameLikelihood < MIN_CONFIDENCE
	) {
		return 0.0
	}
	val angleRad = atan2(
		lastPoint.position.y - midPoint.position.y,
		lastPoint.position.x - midPoint.position.x
	) - atan2(
		firstPoint.position.y - midPoint.position.y,
		firstPoint.position.x - midPoint.position.x
	)
	var angleDeg = Math.toDegrees(angleRad.toDouble())
	angleDeg = abs(angleDeg)
	if (angleDeg > 180) {
		angleDeg = 360 - angleDeg
	}
	return angleDeg
}
