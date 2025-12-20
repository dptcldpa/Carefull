package com.cases.carefull.features.carefullcontents.util

import com.google.mlkit.vision.pose.PoseLandmark

object PoseDefaults {
    val BODY_CONNECTIONS = listOf(
        // 팔
        PoseLandmark.RIGHT_SHOULDER to PoseLandmark.RIGHT_ELBOW,
        PoseLandmark.RIGHT_ELBOW to PoseLandmark.RIGHT_WRIST,
        PoseLandmark.LEFT_SHOULDER to PoseLandmark.LEFT_ELBOW,
        PoseLandmark.LEFT_ELBOW to PoseLandmark.LEFT_WRIST,
        // 상체
        PoseLandmark.LEFT_SHOULDER to PoseLandmark.RIGHT_SHOULDER,
        PoseLandmark.LEFT_HIP to PoseLandmark.LEFT_KNEE,
        PoseLandmark.RIGHT_SHOULDER to PoseLandmark.RIGHT_HIP,
        PoseLandmark.LEFT_SHOULDER to PoseLandmark.LEFT_HIP,
        // 다리
        PoseLandmark.LEFT_HIP to PoseLandmark.RIGHT_HIP,
        PoseLandmark.LEFT_KNEE to PoseLandmark.LEFT_ANKLE,
        PoseLandmark.RIGHT_HIP to PoseLandmark.RIGHT_KNEE,
        PoseLandmark.RIGHT_KNEE to PoseLandmark.RIGHT_ANKLE
    )
}
