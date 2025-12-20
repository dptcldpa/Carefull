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
import com.cases.carefull.domain.model.routine.exercise.Landmark
import com.cases.carefull.domain.model.routine.exercise.Pose
import com.cases.carefull.domain.model.routine.exercise.Position
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme
import com.cases.carefull.features.carefullcontents.util.PoseDefaults
import com.google.mlkit.vision.pose.PoseLandmark

@Composable
fun PoseOverlay(
    modifier: Modifier = Modifier,
    pose: Pose,
    imageWidth: Int,
    imageHeight: Int
) {
    Canvas(modifier = modifier) {
        pose.let { domainPose ->
            val landmarks = domainPose.landmarks.values
            val scaleX = size.width / imageWidth
            val scaleY = size.height / imageHeight
            val points = landmarks
                .filter { it.inFrameLikelihood > 0.7f }
                .map { landmark ->
                    Offset(landmark.position.x * scaleX, landmark.position.y * scaleY)
                }
            drawPoints(points, PointMode.Points, Color.Yellow, strokeWidth = 12f)
            PoseDefaults.BODY_CONNECTIONS.forEach { (startId, endId) ->
                val start = pose.landmarks[startId]
                val end = pose.landmarks[endId]

                if (start != null && end != null &&
                    start.inFrameLikelihood > 0.7f && end.inFrameLikelihood > 0.7f
                ) {
                    drawLine(
                        color = Color.White,
                        strokeWidth = 6f,
                        start = Offset(start.position.x * scaleX, start.position.y * scaleY),
                        end = Offset(end.position.x * scaleX, end.position.y * scaleY)
                    )
                }
            }
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
