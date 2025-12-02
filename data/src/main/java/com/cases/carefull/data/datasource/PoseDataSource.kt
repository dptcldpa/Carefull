package com.cases.carefull.data.datasource

import androidx.camera.core.ImageAnalysis
import com.cases.carefull.domain.model.routine.exercise.AnalysisState
import kotlinx.coroutines.flow.Flow

interface PoseDataSource {
    fun getPoseStream(): Flow<AnalysisState>
    fun getAnalyzer(): ImageAnalysis.Analyzer
    fun close()
}
