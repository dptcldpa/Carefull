package com.cases.carefull.features.carefullcontents.routine.exercise

import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseState
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.model.exercise.Pose
import java.time.LocalDate

data class ExerciseUiState(
	val count: Int = 0,
	val userPose: ExerciseState = ExerciseState.NONE,
	val detectedPose: Pose? = null,
	
	val isLoading: Boolean = true,
	val isError: Boolean = false,
	
	var showDialog: Boolean = false,
	val selectedExercise: ExerciseType?=null,
	val exercisesResults: List<ExerciseCollection> = emptyList(),
	val exerciseCounts: Map<ExerciseType, Int> = emptyMap(),
	val exerciseList: List<ExerciseUiModel> = emptyList(),
	
	val dailyExercise:List<ExerciseType> = emptyList(),
	
	val totalExerciseCounts: Map<ExerciseType, Int> = emptyMap(),

	val weeklyExerciseCounts: Map<ExerciseType, Int> = emptyMap(),
	val dailyExerciseCounts: Map<ExerciseType, Int> = emptyMap(),

	val completedDailyExerciseDates: Set<LocalDate> = emptySet(),
	val analysisState: StreamAnalysisState = StreamAnalysisState.DETECTING_FACE
)

enum class StreamAnalysisState {
	DETECTING_FACE,
	FACE_DETECTED_SUCCESS,
	ANALYZING_EXERCISE
}