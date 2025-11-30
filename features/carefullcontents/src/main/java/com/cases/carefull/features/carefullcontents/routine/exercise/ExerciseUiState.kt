package com.cases.carefull.features.carefullcontents.routine.exercise

import com.cases.carefull.domain.model.exercise.ExerciseState
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.model.exercise.Pose
import java.time.LocalDate

data class ExerciseUiState(
	val isLoading: Boolean = false,
	val isError: Boolean = false,
	val showDialog: Boolean = false,

	val exerciseList: List<ExerciseUiModel> = emptyList(),
	val dailyExercise:List<ExerciseType> = emptyList(),

	val selectedExercise: ExerciseType?=null,
	val count: Int = 0,
	val userPose: ExerciseState = ExerciseState.NONE,
	val detectedPose: Pose? = null,
	val analysisState: StreamAnalysisState = StreamAnalysisState.DETECTING_FACE,

	val completedDailyExerciseDates: Set<LocalDate> = emptySet()
)

enum class StreamAnalysisState {
	DETECTING_FACE,
	FACE_DETECTED_SUCCESS,
	ANALYZING_EXERCISE
}




//	val exercisesResults: List<ExerciseCollection> = emptyList(),
//	val exerciseCounts: Map<ExerciseType, Int> = emptyMap(),
//	val totalExerciseCounts: Map<ExerciseType, Int> = emptyMap(),
//	val weeklyExerciseCounts: Map<ExerciseType, Int> = emptyMap(),
//	val dailyExerciseCounts: Map<ExerciseType, Int> = emptyMap(),