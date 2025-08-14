package com.cases.carefull.domain.model.exercise

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

enum class ExerciseState {
	DOWN, UP, NONE
}