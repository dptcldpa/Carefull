package com.cases.carefull.features.carefullcontents.routine.exercise

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cases.carefull.domain.model.routine.exercise.AnalysisState
import com.cases.carefull.domain.model.routine.exercise.ExerciseAnalyzer
import com.cases.carefull.domain.model.routine.exercise.ExerciseCollection
import com.cases.carefull.domain.model.routine.exercise.ExerciseState
import com.cases.carefull.domain.model.routine.exercise.ExerciseStatistics
import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.domain.model.routine.exercise.Pose
import com.cases.carefull.domain.repository.routine.exercise.PoseRepository
import com.cases.carefull.domain.repository.routine.exercise.TodayWorkOutRepository
import com.cases.carefull.domain.repository.routine.exercise.WorkOutRecordRepository
import com.cases.carefull.domain.usecase.routine.exercise.CalculateWorkOutStatsUseCase
import com.cases.carefull.domain.usecase.routine.exercise.GetWorkOutAnalyzerUseCase
import com.cases.carefull.domain.usecase.routine.exercise.WorkOutCounterUseCase
import com.cases.carefull.domain.util.DataResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val workOutRecordRepository: WorkOutRecordRepository,
    private val todayWorkOutRepository: TodayWorkOutRepository,
    private val poseRepository: PoseRepository,
    private val getWorkOutAnalyzerUseCase: GetWorkOutAnalyzerUseCase,
    private val calculateWorkOutStatsUseCase: CalculateWorkOutStatsUseCase,
    private val workOutCounterUseCase: WorkOutCounterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState = _uiState.asStateFlow()
    private val userId = "test"
    private lateinit var workOutAnalyzer: ExerciseAnalyzer

    init {
        setupDataStream()
        subscribeAnalysisStream()
    }

    fun initialize(exerciseType: ExerciseType) {
        this.workOutAnalyzer = getWorkOutAnalyzerUseCase(exerciseType)
        _uiState.update {
            it.copy(
                count = 0,
                userPose = ExerciseState.NONE,
                detectedPose = null,
                selectedExercise = exerciseType,
                analysisState = StreamAnalysisState.DETECTING_FACE
            )
        }
    }

    fun getCameraAnalyzer(): Any {
        return poseRepository.createPoseAnalyzer()
    }

    private fun setupDataStream() {
        val statsFlow = workOutRecordRepository.fetchWorkOutStats(userId)
        val todayExerciseFlow = todayWorkOutRepository.fetchTodayWorkOut()

        combine(statsFlow, todayExerciseFlow) { statsResult, todayResult ->
            processWorkoutData(statsResult, todayResult)
        }.launchIn(viewModelScope)
    }

    @SuppressLint("DefaultLocale")
    private fun processWorkoutData(
        statsResult: DataResourceResult<List<ExerciseCollection>>,
        todayResult: DataResourceResult<ExerciseType>
    ) {
        val exercises = (statsResult as? DataResourceResult.Success)?.data ?: emptyList()
        val dailyExerciseList =
            (todayResult as? DataResourceResult.Success)?.data?.let { listOf(it) } ?: emptyList()
        val isError =
            statsResult is DataResourceResult.Error || todayResult is DataResourceResult.Error
        val statsList: List<ExerciseStatistics> = calculateWorkOutStatsUseCase(exercises)
        val uiModelList = statsList.map { it.toUiModel() }

        _uiState.update { state ->
            state.copy(
                isLoading = false,
                isError = isError,
//                exercisesResults = exercises,
                dailyExercise = dailyExerciseList,
//                totalExerciseCounts = uiModelList.associate { it.type to it.totalCount },
//                weeklyExerciseCounts = uiModelList.associate { it.type to it.weeklyCount },
//                dailyExerciseCounts = uiModelList.associate { it.type to it.dailyCount },
                exerciseList = uiModelList
            )
        }
    }

    fun saveWorkoutResult(exerciseType: ExerciseType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val totalCount = _uiState.value.count
            val recordToSave = ExerciseCollection(
                userId = userId,
                exerciseType = exerciseType,
                count = totalCount
            )
            val result = workOutRecordRepository.saveWorkOutCount(recordToSave)
            when (result) {
                is DataResourceResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            count = 0,
                            userPose = ExerciseState.NONE,
                            showDialog = true
                        )
                    }
                }

                is DataResourceResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }

                is DataResourceResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun subscribeAnalysisStream() {
        poseRepository.getPoseAnalysisStream()
            .onEach { state -> handleAnalysisState(state) }
            .catch { _uiState.update { it.copy(isError = true) } }
            .launchIn(viewModelScope)
    }

    private fun handleAnalysisState(state: AnalysisState) {
        poseRepository.getPoseAnalysisStream()
            .onEach { state ->
                when (state) {
                    is AnalysisState.SearchingForFace -> {
                        _uiState.update { it.copy(analysisState = StreamAnalysisState.DETECTING_FACE) }
                    }

                    is AnalysisState.FaceDetected -> {
                        viewModelScope.launch {
                            _uiState.update { it.copy(analysisState = StreamAnalysisState.FACE_DETECTED_SUCCESS) }
                            delay(1500)
                            _uiState.update { it.copy(analysisState = StreamAnalysisState.ANALYZING_EXERCISE) }
                        }
                    }

                    is AnalysisState.AnalyzingPose -> {
                        if (_uiState.value.analysisState != StreamAnalysisState.ANALYZING_EXERCISE) {
                            _uiState.update { it.copy(analysisState = StreamAnalysisState.ANALYZING_EXERCISE) }
                        }
                        processPoseDetection(state.pose)
                    }
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isError = true) }
            }
            .launchIn(viewModelScope)
    }

    private fun processPoseDetection(pose: Pose) {
        if (!::workOutAnalyzer.isInitialized) return

        val newDetectedState = workOutAnalyzer.analyze(pose)
        val currentState = _uiState.value
        val result = workOutCounterUseCase(
            currentCount = currentState.count,
            lastConfirmedPose = currentState.userPose,
            newDetectedPose = newDetectedState
        )

        _uiState.update { state ->
            state.copy(
                count = result.count,
                userPose = result.currentPose,
                detectedPose = pose
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        poseRepository.closeAnalyzer()
    }
}
