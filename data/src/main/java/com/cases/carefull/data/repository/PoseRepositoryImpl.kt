package com.cases.carefull.data.repository

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.content.ContextCompat
import com.cases.carefull.data.mapper.toDomainPose
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.repository.PoseRepository
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

    private var poseDetector: PoseDetector? = null
    private val executor: Executor = ContextCompat.getMainExecutor(context)
    private lateinit var imageAnalysis: ImageAnalysis

    private val poseFlow: Flow<Pose> = callbackFlow {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        poseDetector = PoseDetection.getClient(options)

        val mlKitAnalyzer = MlKitAnalyzer(
            listOf(poseDetector!!),
            ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
            executor
        ) { result: MlKitAnalyzer.Result? ->
            val mlKitPose = result?.getValue(poseDetector!!)
            if (mlKitPose != null) {
                val domainPose = mlKitPose.toDomainPose()
                trySend(domainPose)
            }
        }
        imageAnalysis = ImageAnalysis.Builder()
            .build()
            .apply {
                setAnalyzer(executor, mlKitAnalyzer)
            }
        awaitClose {
            poseDetector?.close()
        }
    }

    override fun analyze(): Flow<Pose> {
        return poseFlow
    }

    override fun getCameraUseCase(): Any {
        if (!::imageAnalysis.isInitialized) {
            throw IllegalStateException("analyze() must be called before getCameraUseCase()")
        }
        return imageAnalysis
    }

    override fun close() {
        poseDetector?.close()
    }
}
