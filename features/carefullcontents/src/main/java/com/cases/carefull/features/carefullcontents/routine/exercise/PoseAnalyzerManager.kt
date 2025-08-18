package com.cases.carefull.features.carefullcontents.routine.exercise

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.content.ContextCompat
import com.cases.carefull.data.mapper.toDomainPose
import com.cases.carefull.domain.model.exercise.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.util.concurrent.Executor

/**
 * ML Kit PoseDetector와 MlKitAnalyzer의 생성 및 설정을 관리하는 클래스.
 * UI 코드(Screen)로부터 복잡한 ML Kit 설정 로직을 분리하는 역할을 합니다.
 */
class PoseAnalyzerManager {
    private fun getExecutor(context: Context): Executor {
        return ContextCompat.getMainExecutor(context)
    }

    fun build(
        context: Context,
        onPoseDetected: (Pose) -> Unit
    ): ImageAnalysis.Analyzer {
        // 1. ML Kit PoseDetector 옵션 설정
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()

        // 2. PoseDetector 클라이언트 생성
        val poseDetector = PoseDetection.getClient(options)

        // 3. MlKitAnalyzer 생성
        return MlKitAnalyzer(
            listOf(poseDetector),
            ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
            getExecutor(context)
        ) { result: MlKitAnalyzer.Result? ->
            val mlKitPose = result?.getValue(poseDetector)
            if (mlKitPose != null) {
                // 4. 감지된 결과를 도메인 모델로 변환하여 외부 콜백 호출
                val domainPose = mlKitPose.toDomainPose()
                onPoseDetected(domainPose)
            }
        }
    }
}