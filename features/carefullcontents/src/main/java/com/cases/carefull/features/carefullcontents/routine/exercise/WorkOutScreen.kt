package com.cases.carefull.features.carefullcontents.routine.exercise

import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.cases.carefull.data.PoseAnalyzer
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WorkOutScreen(
	viewModel: ExerciseViewModel,
	navController: NavController
) {
	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	
	val route = navController.currentBackStackEntry?.toRoute<RoutineRoute.WorkOutScreen>()
	val exerciseType = route?.exerciseType
	
	val imageAnalyzer = remember(context) {
		ImageAnalysis.Builder()
			.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
			.build()
	}
	// 이미지 분석 결과의 해상도를 저장하기 위한 상태
	var imageWidth by remember { mutableIntStateOf(480) }
	var imageHeight by remember { mutableIntStateOf(640) }
	
	DisposableEffect(lifecycleOwner, imageAnalyzer) {
		val poseAnalyzer = PoseAnalyzer(
			onPoseDetected = { imageProxy, mlKitPose ->
				val mediaImage = imageProxy.image
				if (mediaImage != null) {
					val image = InputImage.fromMediaImage(
						mediaImage,
						imageProxy.imageInfo.rotationDegrees
					)
					viewModel.processImage(image)
				}
				imageProxy.close()
			}
		)
		// 분석기 설정
		imageAnalyzer.setAnalyzer(ContextCompat.getMainExecutor(context), poseAnalyzer)
		// onDispose: 이 Composable이 화면에서 사라질 때(popBackStack 등) 호출되는 정리 블록
		onDispose {
			imageAnalyzer.clearAnalyzer() // 분석기 제거 (매우 중요!)
			viewModel.cleanup()           // ViewModel 정리 함수 호출
		}
	}
	
	LaunchedEffect(exerciseType) {
		if (exerciseType != null) {
			viewModel.initialize(exerciseType)
		}
	}
	
	// 카메라 권한 요청
	val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
	LaunchedEffect(key1 = Unit) {
		if (!cameraPermissionState.status.isGranted) {
			cameraPermissionState.launchPermissionRequest()
		}
	}
	
	if (cameraPermissionState.status.isGranted) {
		Box(modifier = Modifier.fillMaxSize()) {
			CameraView(
				modifier = Modifier.fillMaxSize(),
				lifecycleOwner = lifecycleOwner,
				analyzer = imageAnalyzer
			)
			val detectedPose = uiState.detectedPose
			if (detectedPose != null) {
				PoseOverlay(
					pose = detectedPose,
					modifier = Modifier.fillMaxSize(),
					imageWidth = imageWidth,
					imageHeight = imageHeight
				)
			}
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				horizontalAlignment = Alignment.End
			) {
				Text(
					text = "$exerciseType",
					style = MaterialTheme.typography.headlineMedium,
					color = MaterialTheme.colorScheme.primary
				)
				Spacer(modifier = Modifier.height(16.dp))
				Text(
					text = "횟수: ${uiState.count}",
					fontSize = 32.sp,
					color = MaterialTheme.colorScheme.onSurface
				)
				Text(
					text = "자세: ${uiState.userPose}",
					fontSize = 24.sp,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
				)
				Button(
					onClick = {
						if (exerciseType != null) {
							viewModel.saveWorkoutResult(exerciseType)
						}
						navController.popBackStack()
					}
				) {
					Text("종료")
				}
			}
		}
	} else {
		// 권한이 거부된 경우
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text("카메라 권한을 허용해주세요.")
		}
	}
}

@Composable
fun CameraView(
	modifier: Modifier = Modifier,
	lifecycleOwner: LifecycleOwner,
	analyzer: ImageAnalysis
) {
	val context = LocalContext.current
	val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
	
	AndroidView(
		factory = { ctx ->
			PreviewView(ctx).apply {
				this.scaleType = PreviewView.ScaleType.FILL_CENTER
			}
		},
		modifier = modifier,
		update = { previewView ->
			cameraProviderFuture.addListener({
				val cameraProvider = cameraProviderFuture.get()
				
				val preview = Preview.Builder().build().also {
					it.surfaceProvider = previewView.surfaceProvider
				}
				val cameraSelector = CameraSelector.Builder()
					.requireLensFacing(CameraSelector.LENS_FACING_BACK)
					.build()
				try {
					cameraProvider.unbindAll()
					cameraProvider.bindToLifecycle(
						lifecycleOwner,
						cameraSelector,
						preview,
						analyzer
					)
				} catch (e: Exception) {
					Log.e("CameraView", "Use case binding failed", e)
				}
			}, ContextCompat.getMainExecutor(context))
		}
	)
}