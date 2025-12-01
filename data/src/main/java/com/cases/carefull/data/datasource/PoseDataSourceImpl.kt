package com.cases.carefull.data.datasource

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.content.ContextCompat
import com.cases.carefull.data.mapper.toDomainPose
import com.cases.carefull.domain.model.exercise.AnalysisState
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class PoseDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : PoseDataSource {

    private val _analysisStateFlow = MutableSharedFlow<AnalysisState>(
        replay = 1,
        extraBufferCapacity = 60,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private var mlKitAnalyzer: MlKitAnalyzer? = null
    private var faceDetector: FaceDetector? = null
    private var poseDetector: PoseDetector? = null
    private var isFaceDetectedAndReady = false

    override fun getPoseStream(): Flow<AnalysisState> = _analysisStateFlow.asSharedFlow()


    override fun getAnalyzer(): ImageAnalysis.Analyzer {
        if (mlKitAnalyzer != null) return mlKitAnalyzer!!

        val faceOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE) // 수정: setContourMode
            .build()
        faceDetector = FaceDetection.getClient(faceOptions)

        val poseOptions = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        poseDetector = PoseDetection.getClient(poseOptions)

        val analyzer = MlKitAnalyzer(
            listOf(faceDetector!!, poseDetector!!),
            ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
            ContextCompat.getMainExecutor(context)
        ) { result ->
            processResult(result)
        }
        mlKitAnalyzer = analyzer
        return analyzer
    }

    override fun close() {
        faceDetector?.close()
        poseDetector?.close()
        faceDetector = null
        poseDetector = null
        mlKitAnalyzer = null
        isFaceDetectedAndReady = false
    }

    private fun processResult(result: MlKitAnalyzer.Result) {
        val currentFaceDetector = faceDetector ?: return
        val currentPoseDetector = poseDetector ?: return

        val face = result.getValue(currentFaceDetector)
        val poseResult = result.getValue(currentPoseDetector)

        val newState = if (!isFaceDetectedAndReady) {
            if (!face.isNullOrEmpty()) {
                isFaceDetectedAndReady = true
                AnalysisState.FaceDetected
            } else {
                AnalysisState.SearchingForFace
            }
        } else {
            if (!face.isNullOrEmpty()) {
                poseResult?.let { pose ->
                    AnalysisState.AnalyzingPose(pose.toDomainPose())
                } ?: AnalysisState.SearchingForFace
            } else {
                isFaceDetectedAndReady = false
                AnalysisState.SearchingForFace
            }
        }
        _analysisStateFlow.tryEmit(newState)
    }
}
