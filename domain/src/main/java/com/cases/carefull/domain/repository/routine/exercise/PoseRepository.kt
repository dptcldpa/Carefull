package com.cases.carefull.domain.repository.routine.exercise

import com.cases.carefull.domain.model.routine.exercise.AnalysisState
import kotlinx.coroutines.flow.Flow

interface PoseRepository {
    fun getPoseAnalysisStream(): Flow<AnalysisState>
    fun createPoseAnalyzer(): Any
    fun closeAnalyzer()
}
