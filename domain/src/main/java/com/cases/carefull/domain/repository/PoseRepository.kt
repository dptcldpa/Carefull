package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.exercise.AnalysisState
import kotlinx.coroutines.flow.Flow

interface PoseRepository {
    fun analyze(): Flow<AnalysisState>

    fun getCameraUseCase(): Any

    fun close()
}