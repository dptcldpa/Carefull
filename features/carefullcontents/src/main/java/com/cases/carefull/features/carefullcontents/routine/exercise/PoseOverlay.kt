package com.cases.carefull.features.carefullcontents.routine.exercise

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.tooling.preview.Preview
import com.cases.carefull.domain.model.exercise.Landmark
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.model.exercise.Position
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme
import com.google.mlkit.vision.pose.PoseLandmark

@Composable
fun PoseOverlay(
    pose: Pose,
    modifier: Modifier = Modifier,
    imageWidth: Int,
    imageHeight: Int
) {
    Canvas(modifier = modifier) {
        pose.let { domainPose ->
            val landmarks = domainPose.landmarks.values

            // 화면 크기와 이미지 크기 간의 비율 계산
            val scaleX = size.width / imageWidth
            val scaleY = size.height / imageHeight

            // 관절(랜드마크) 그리기
            val points = landmarks
                .filter { it.inFrameLikelihood > 0.7f } // 신뢰도 높은 점만 그리기
                .map { landmark ->
                    Offset(landmark.position.x * scaleX, landmark.position.y * scaleY)
                }
            drawPoints(points, PointMode.Points, Color.Yellow, strokeWidth = 12f)

            // 연결선 그리기 함수
            fun drawLine(startType: Int, endType: Int) {
                val startLandmark = domainPose.landmarks[startType]
                val endLandmark = domainPose.landmarks[endType]

                if (startLandmark != null && endLandmark != null &&
                    startLandmark.inFrameLikelihood > 0.7f && endLandmark.inFrameLikelihood > 0.7f
                ) {
                    drawLine(
                        start = Offset(
                            startLandmark.position.x * scaleX,
                            startLandmark.position.y * scaleY
                        ),
                        end = Offset(
                            endLandmark.position.x * scaleX,
                            endLandmark.position.y * scaleY
                        ),
                        color = Color.White,
                        strokeWidth = 6f
                    )
                }
            }
            // 팔
            drawLine(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW)
            drawLine(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST)
            drawLine(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW)
            drawLine(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST)

            // 상체
            drawLine(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER)
            drawLine(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP)
            drawLine(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP)
            drawLine(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP)

            // 다리
            drawLine(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE)
            drawLine(PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE)
            drawLine(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE)
            drawLine(PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE)
        }
    }
}

@Preview
@Composable
fun PoseOverlayPreview() {
    val imageWidth = 480
    val imageHeight = 640

    val fakePose = createFakePose()

    CarefullTheme {
        PoseOverlay(
            pose = fakePose,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            imageWidth = imageWidth,
            imageHeight = imageHeight
        )
    }
}

private fun createFakePose(): Pose {
    val landmarks = mapOf(
        // 어깨
        PoseLandmark.RIGHT_SHOULDER to Landmark(
            PoseLandmark.RIGHT_SHOULDER,
            Position(180f, 150f),
            1f
        ),
        PoseLandmark.LEFT_SHOULDER to Landmark(
            PoseLandmark.LEFT_SHOULDER,
            Position(300f, 150f),
            1f
        ),
        // 팔꿈치
        PoseLandmark.RIGHT_ELBOW to Landmark(PoseLandmark.RIGHT_ELBOW, Position(140f, 250f), 1f),
        PoseLandmark.LEFT_ELBOW to Landmark(PoseLandmark.LEFT_ELBOW, Position(340f, 250f), 1f),
        // 손목
        PoseLandmark.RIGHT_WRIST to Landmark(PoseLandmark.RIGHT_WRIST, Position(110f, 340f), 1f),
        PoseLandmark.LEFT_WRIST to Landmark(PoseLandmark.LEFT_WRIST, Position(370f, 340f), 1f),
        // 엉덩이
        PoseLandmark.RIGHT_HIP to Landmark(PoseLandmark.RIGHT_HIP, Position(200f, 350f), 1f),
        PoseLandmark.LEFT_HIP to Landmark(PoseLandmark.LEFT_HIP, Position(280f, 350f), 1f),
        // 무릎
        PoseLandmark.RIGHT_KNEE to Landmark(PoseLandmark.RIGHT_KNEE, Position(210f, 480f), 1f),
        PoseLandmark.LEFT_KNEE to Landmark(PoseLandmark.LEFT_KNEE, Position(270f, 480f), 1f),
        // 발목
        PoseLandmark.RIGHT_ANKLE to Landmark(PoseLandmark.RIGHT_ANKLE, Position(220f, 600f), 1f),
        PoseLandmark.LEFT_ANKLE to Landmark(PoseLandmark.LEFT_ANKLE, Position(260f, 600f), 1f)
    )
    return Pose(landmarks = landmarks)
}