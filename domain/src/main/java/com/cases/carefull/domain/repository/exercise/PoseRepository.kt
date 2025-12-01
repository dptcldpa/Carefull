package com.cases.carefull.domain.repository.exercise

import com.cases.carefull.domain.model.exercise.AnalysisState
import kotlinx.coroutines.flow.Flow

interface PoseRepository {
    fun getPoseAnalysisStream(): Flow<AnalysisState>
    fun createPoseAnalyzer(): Any
    fun closeAnalyzer()
}
