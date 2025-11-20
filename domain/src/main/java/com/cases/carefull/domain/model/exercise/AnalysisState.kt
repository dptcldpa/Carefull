package com.cases.carefull.domain.model.exercise

sealed class AnalysisState {
    object SearchingForFace : AnalysisState()

    object FaceDetected : AnalysisState()

    data class AnalyzingPose(val pose: Pose) : AnalysisState()
}