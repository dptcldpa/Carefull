package com.cases.carefull.features.carefullcontents.routine.exercise

import android.Manifest
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
import androidx.compose.runtime.remember
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
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

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
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }
    val poseAnalyzerManager = remember { PoseAnalyzerManager() }
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    
    DisposableEffect(poseAnalyzerManager, imageAnalysis) {
        val analyzer = poseAnalyzerManager.build(context) { domainPose ->
            viewModel.onPoseDetected(domainPose)
        }
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer)
        onDispose {
            imageAnalysis.clearAnalyzer()
        }
    }
    
    LaunchedEffect(exerciseType) {
        if (exerciseType != null) {
            viewModel.initialize(exerciseType)
        }
    }
    
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
                analyzer = imageAnalysis
            )
            val detectedPose = uiState.detectedPose
            if (detectedPose != null) {
                PoseOverlay(
                    pose = detectedPose,
                    modifier = Modifier.fillMaxSize(),
                    imageWidth = 480,
                    imageHeight = 640
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = exerciseType!!.type,
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
                        viewModel.saveWorkoutResult(exerciseType)
                        navController.popBackStack()
                    }
                ) {
                    Text("종료")
                }
            }
        }
    } else {
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
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}