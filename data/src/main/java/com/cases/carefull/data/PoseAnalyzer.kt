package com.cases.carefull.data

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

class PoseAnalyzer(
	private val onPoseDetected: (ImageProxy, Pose) -> Unit
) : ImageAnalysis.Analyzer {
	
	private val options = PoseDetectorOptions.Builder()
		.setDetectorMode(PoseDetectorOptions.STREAM_MODE)
		.build()
	private val poseDetector = PoseDetection.getClient(options)
	
	@SuppressLint("UnsafeOptInUsageError")
	override fun analyze(imageProxy: ImageProxy) {
		val mediaImage = imageProxy.image
		if (mediaImage != null) {
			val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
			poseDetector.process(image)
				.addOnSuccessListener { pose ->
					onPoseDetected(imageProxy, pose)
				}
				.addOnFailureListener {
					imageProxy.close()
				}
		}
	}
}