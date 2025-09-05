package com.cases.carefull.features.carefullcontents.routine.exercise

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseState
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.model.exercise.analyzer.DumbbellCurlAnalyzer
import com.cases.carefull.domain.model.exercise.analyzer.DumbbellShoulderPressAnalyzer
import com.cases.carefull.domain.model.exercise.analyzer.PushUpAnalyzer
import com.cases.carefull.domain.model.exercise.analyzer.SquatAnalyzer
import com.cases.carefull.domain.repository.ExerciseAnalyzer
import com.cases.carefull.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import kotlin.collections.map

@RequiresApi(Build.VERSION_CODES.O)
class ExerciseViewModel(
	private val exerciseRepository: ExerciseRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(ExerciseUiState())
	val uiState = _uiState.asStateFlow()
	private lateinit var analyzer: ExerciseAnalyzer
	
	companion object {
		const val DAILY_EXERCISE_GOAL = 30
	}
	
	init {
		loadInitialData()
	}
	
	@RequiresApi(Build.VERSION_CODES.O)
	fun loadInitialData() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			try {
				val exercises = exerciseRepository.getExerciseStat(userId = "test")
				val dailyExercises = exerciseRepository.getDailyExerciseList()
				val completedDates = exerciseRepository.getCompletedDailyExerciseDates("test")
				val totalCountsMap = exercises.associate {
					it.exerciseType to it.count
				}
				val weeklyCountsMap = calculateWeeklyCounts(exercises)
				val dailyCountsMap = calculateDailyCounts(exercises)
				val groupedBySport = exercises.groupBy { it.exerciseType }
				val countsMap = groupedBySport.mapValues { (_, records) ->
					records.sumOf { it.count }
					}
				val uiModelList = ExerciseType.entries.map { exerciseType ->
					val count = countsMap[exerciseType]?:0
					val weeklyCount = weeklyCountsMap[exerciseType] ?: 0
					val dailyCount = dailyCountsMap[exerciseType] ?: 0
					//
					exerciseType.toUiModel(count, weeklyCount, dailyCount)
				}
				_uiState.update {
					it.copy(
						isLoading = false,
						exercisesResults = exercises,
						
						totalExerciseCounts = totalCountsMap,
						weeklyExerciseCounts = weeklyCountsMap,
						dailyExerciseCounts = dailyCountsMap,
						completedDailyExerciseDates = completedDates,
						
						exerciseCounts = countsMap,
						exerciseList = uiModelList,
						dailyExercise = dailyExercises
					)
				}
			} catch (e: Exception) {
				_uiState.update { it.copy(isLoading = false, isError = true) }
			}
		}
	}
	private fun calculateDailyCounts(exercises: List<ExerciseCollection>): Map<ExerciseType, Int> {
		val currentDailyKey = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
		return exercises.associate {
			it.exerciseType to (it.dailyCounts[currentDailyKey] ?: 0)
		}
	}
	
	@SuppressLint("DefaultLocale")
	private fun calculateWeeklyCounts(exercises: List<ExerciseCollection>): Map<ExerciseType, Int> {
		val today = LocalDate.now()
		val weekFields = WeekFields.ISO
		
		val weekBasedYear = today.get(weekFields.weekBasedYear())
		val weekOfYear = today.get(weekFields.weekOfWeekBasedYear())
		
		val currentWeekKey = "${weekBasedYear}-W${String.format("%02d", weekOfYear)}"
		
		return exercises.associate {
			it.exerciseType to (it.weeklyCounts[currentWeekKey] ?: 0)
		}
	}
	
	fun initialize(exerciseType: ExerciseType) {
		this.analyzer = createAnalyzer(exerciseType)
		_uiState.update {
			it.copy(
				count = 0,
				userPose = ExerciseState.NONE,
				detectedPose = null
			)
		}
	}
	
	fun onPoseDetected(pose: Pose) {
		if (!::analyzer.isInitialized) return
		val newDetectedState = analyzer.analyze(pose)
		_uiState.update {
			val newCount =
				if (it.userPose == ExerciseState.DOWN && newDetectedState == ExerciseState.UP) {
					it.count + 1
				} else {
					it.count
				}
			val newUserPose =
				if (newDetectedState == ExerciseState.UP || newDetectedState == ExerciseState.DOWN) {
					newDetectedState
				} else {
					it.userPose
				}
			it.copy(
				count = newCount,
				userPose = newUserPose,
				detectedPose = pose
			)
		}
	}
	
	@RequiresApi(Build.VERSION_CODES.O)
	fun saveWorkoutResult(exerciseType: ExerciseType) {
		viewModelScope.launch {
			val totalCount = _uiState.value.count
			
			if (totalCount > 0) {
				val recordToSave = ExerciseCollection(
					userId = "test",
					exerciseType = exerciseType,
					count = totalCount
				)
				val result = exerciseRepository.addExerciseRecord(recordToSave)
				if (result) {
					loadInitialData()
				} else {
					_uiState.update { it.copy(isError = true) }
				}
			}
		}
	}
	
	fun onExerciseSelected(exerciseType: ExerciseType) {
		_uiState.update {
			it.copy(
				selectedExercise = exerciseType,
				showDialog = true
			)
		}
	}
	
	fun onDialogDismiss() {
		_uiState.update { it.copy(showDialog = false) }
	}
	
	fun onDialogConfirm() {
		_uiState.update { it.copy(showDialog = false) }
	}
	
	private fun createAnalyzer(type: ExerciseType): ExerciseAnalyzer {
		return when (type) {
			ExerciseType.DUMBBELL_CURL -> DumbbellCurlAnalyzer(isLeftHand = false)
			ExerciseType.SQUAT -> SquatAnalyzer()
			ExerciseType.PUSH_UP -> PushUpAnalyzer(isLeftHand = false)
			ExerciseType.DUMBBELL_SHOULDER_PRESS -> DumbbellShoulderPressAnalyzer(isLeftHand = false)
		}
	}
}