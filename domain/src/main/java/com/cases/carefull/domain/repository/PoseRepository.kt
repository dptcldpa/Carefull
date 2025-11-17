package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.exercise.Pose
import kotlinx.coroutines.flow.Flow

interface PoseRepository {
    fun analyze(): Flow<Pose>

    fun getCameraUseCase(): Any

    fun close()
}