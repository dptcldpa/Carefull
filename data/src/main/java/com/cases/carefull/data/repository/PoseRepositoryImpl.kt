package com.cases.carefull.data.repository

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.content.ContextCompat
import com.cases.carefull.data.mapper.toDomainPose
import com.cases.carefull.domain.model.exercise.AnalysisState
import com.cases.carefull.domain.repository.PoseRepository
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Executor
import javax.inject.Inject

class PoseRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PoseRepository {

    private var faceDetector: FaceDetector? = null
    private var poseDetector: PoseDetector? = null
    private val executor: Executor = ContextCompat.getMainExecutor(context)
    private lateinit var imageAnalysis: ImageAnalysis

    private val analysisFlow: Flow<AnalysisState> = callbackFlow {

        var isFaceDetectedAndReady = false

        val faceOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setPerformanceMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .build()
        faceDetector = FaceDetection.getClient(faceOptions)

        val poseOptions = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        poseDetector = PoseDetection.getClient(poseOptions)

        val mlKitAnalyzer = MlKitAnalyzer(
            listOf(faceDetector!!, poseDetector!!),
            ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
            executor
        ) { result: MlKitAnalyzer.Result? ->

            val face = result?.getValue(faceDetector!!)

            if (!isFaceDetectedAndReady) {
                val face = result?.getValue(faceDetector!!)
                if (face != null && face.isNotEmpty()) {
                    isFaceDetectedAndReady = true
                    trySend(AnalysisState.FaceDetected)
                } else {
                    trySend(AnalysisState.SearchingForFace)
                }
            } else {
                if (face != null && face.isNotEmpty()) {
                    val mlKitPose = result.getValue(poseDetector!!)
                    if (mlKitPose != null) {
                        val domainPose = mlKitPose.toDomainPose()
                        trySend(AnalysisState.AnalyzingPose(domainPose))
                    }
                } else {
                    isFaceDetectedAndReady = false
                    trySend(AnalysisState.SearchingForFace)
                }
            }
        }
        imageAnalysis = ImageAnalysis.Builder()
            .build()
            .apply {
                setAnalyzer(executor, mlKitAnalyzer)
            }
        awaitClose {
            faceDetector?.close()
            poseDetector?.close()
        }
    }

    override fun analyze(): Flow<AnalysisState> {
        return analysisFlow
    }

    override fun getCameraUseCase(): Any {
        if (!::imageAnalysis.isInitialized) {
            throw IllegalStateException("analyze() must be called before getCameraUseCase()")
        }
        return imageAnalysis
    }

    override fun close() {
        faceDetector?.close()
        poseDetector?.close()
    }
}
