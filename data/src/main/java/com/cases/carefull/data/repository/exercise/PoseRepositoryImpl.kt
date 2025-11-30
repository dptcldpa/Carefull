package com.cases.carefull.data.repository.exercise

import com.cases.carefull.data.datasource.PoseDataSource
import com.cases.carefull.domain.model.exercise.AnalysisState
import com.cases.carefull.domain.repository.exercise.PoseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PoseRepositoryImpl @Inject constructor(
    private val dataSource: PoseDataSource,
) : PoseRepository {

    override fun getPoseAnalysisStream(): Flow<AnalysisState> {
        return dataSource.getPoseStream()
    }

    override fun createPoseAnalyzer(): Any {
        return dataSource.getAnalyzer()
    }

    override fun closeAnalyzer() {
        dataSource.close()
    }
}
