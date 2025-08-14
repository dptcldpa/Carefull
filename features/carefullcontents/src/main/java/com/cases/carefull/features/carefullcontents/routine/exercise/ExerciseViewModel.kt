package com.cases.carefull.features.carefullcontents.routine.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseState
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.model.exercise.analyzer.DumbbellCurlAnalyzer
import com.cases.carefull.domain.model.exercise.analyzer.DumbbellShoulderPressAnalyzer
import com.cases.carefull.domain.repository.ExerciseAnalyzer
import com.cases.carefull.domain.model.exercise.analyzer.PushUpAnalyzer
import com.cases.carefull.domain.model.exercise.analyzer.SquatAnalyzer
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.util.DataResult
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExerciseViewModel(
	private val exerciseRepository: ExerciseRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(ExerciseUiState())
	val uiState = _uiState.asStateFlow()
	private var lastState: ExerciseState = ExerciseState.NONE
	private lateinit var analyzer: ExerciseAnalyzer
	val exerciseListForUi: StateFlow<List<ExerciseUiModel>> = _uiState
		.map { state ->
			ExerciseType.entries.map { exerciseType ->
				val count = state.exerciseCounts[exerciseType] ?: 0
				exerciseType.toUiModel(count)
			}
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)
	
	init {
		fetchAllExercises()
	}
	
	fun initialize(exerciseType: ExerciseType) {
		this.analyzer = createAnalyzer(exerciseType)
		_uiState.update { currentState ->
			currentState.copy(
				count = 0,                        // 현재 운동 횟수만 0으로 리셋
				userPose = ExerciseState.NONE,    // 자세 상태 초기화
				detectedPose = null               // 감지된 포즈 초기화
			)
		}
		lastState = ExerciseState.NONE
	}
	
	fun fetchAllExercises() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			val result = exerciseRepository.getAllExercise(userId = "test")
			when (result) {
				is DataResult.Success -> {
					val exercises = result.data
					val groupedBySport = exercises.groupBy { it.exerciseType }
					
					val countsMap = groupedBySport.mapNotNull { (sportName, records) ->
						val exerciseType = try {
							ExerciseType.valueOf(sportName)
						} catch (e: IllegalArgumentException) {
							null
						}
						exerciseType?.let { type ->
							type to records.sumOf { it.count }
						}
					}.toMap()
					_uiState.update {
						it.copy(
							isLoading = false,
							exercisesResults = exercises,
							exerciseCounts = countsMap
						)
					}
				}
				
				is DataResult.Error -> {
					_uiState.update {
						it.copy(isLoading = false, isError = true)
					}
				}
				
				is DataResult.Loading -> {
					_uiState.update {
						it.copy(isLoading = true)
					}
				}
			}
		}
	}
	
	fun saveWorkoutResult(exerciseType: ExerciseType) {
		viewModelScope.launch {
			val finalCount = _uiState.value.count
			
			if (finalCount > 0) {
				val recordToSave = ExerciseCollection(
					userId = "test",
					exerciseType = exerciseType.name,
					count = finalCount
				)
				val result = exerciseRepository.addExerciseRecord(recordToSave)
				when (result) {
					is DataResult.Success -> {
						fetchAllExercises()
					}
					
					is DataResult.Error -> {
						_uiState.update { it.copy(isError = true) }
					}
					
					else -> {}
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
	
	private var analysisJob: Job? = null
	fun processImage(image: InputImage) {
		analysisJob?.cancel()
		analysisJob = viewModelScope.launch {
			exerciseRepository.analyzeImage(image)
				.onSuccess { domainPose ->
					analyzeDomainPose(domainPose)
				}
				.onFailure {
				
				}
		}
	}
	
	private fun analyzeDomainPose(pose: Pose) {
		if (!::analyzer.isInitialized) return
		viewModelScope.launch {
			val currentState = analyzer.analyze(pose)
			
			_uiState.update { it.copy(userPose = currentState, detectedPose = pose) }
			
			if (currentState == ExerciseState.UP && lastState == ExerciseState.DOWN) {
				_uiState.update { currentUiState ->
					currentUiState.copy(count = currentUiState.count + 1)
				}
			}
			if (currentState != ExerciseState.NONE) {
				lastState = currentState
			}
		}
	}
	
	private fun createAnalyzer(type: ExerciseType): ExerciseAnalyzer {
		return when (type) {
			ExerciseType.DUMBBELL_CURL -> DumbbellCurlAnalyzer(isLeftHand = false)
			ExerciseType.SQUAT -> SquatAnalyzer()
			ExerciseType.PUSH_UP -> PushUpAnalyzer(isLeftHand = false)
			ExerciseType.DUMBBELL_SHOULDER_PRESS -> DumbbellShoulderPressAnalyzer(isLeftHand = false)
		}
	}
	
	fun cleanup() {
		analysisJob?.cancel() // 진행 중인 분석 작업 취소
		// 만약 analyzer가 리소스를 사용한다면 여기서 해제
		// (현재 구조에서는 analyzer가 ViewModel에 직접 없으므로 생략)
		Log.d("ViewModelCleanup", "ExerciseViewModel resources cleaned up.")
	}
	
	override fun onCleared() {
		super.onCleared()
		cleanup() // onCleared 시에도 cleanup 로직을 실행
		Log.d("ViewModelCleanup", "ExerciseViewModel onCleared.")
	}
}