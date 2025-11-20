package com.cases.carefull.features.carefullcontents.routine.exercise

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WorkOutRoute(
    viewModel: ExerciseViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val route = navController.currentBackStackEntry?.toRoute<RoutineRoute.WorkOutRoute>()
    val exerciseType = route?.exerciseType
    val imageAnalysisUseCase = remember { viewModel.getCameraAnalysisUseCase() }
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    WorkOutScreen(
        imageAnalysisUseCase = imageAnalysisUseCase,
        hasPermission = cameraPermissionState.status.isGranted,
        requestPermission = { cameraPermissionState.launchPermissionRequest() },
        uiState = uiState,
        onStopClick = {
            if (exerciseType != null) {
                viewModel.saveWorkoutResult(exerciseType)
            }
            navController.popBackStack()
        },
        initialize = {
            if (exerciseType != null) {
                viewModel.initialize(exerciseType)
            }
        },
        exerciseType = exerciseType
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WorkOutScreen(
    imageAnalysisUseCase: UseCase,
    hasPermission: Boolean,
    requestPermission: () -> Unit,
    uiState: ExerciseUiState,
    exerciseType: ExerciseType?,
    onStopClick: () -> Unit,
    initialize: () -> Unit,
    isPreview: Boolean = false
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    if (!isPreview) {
        LaunchedEffect(exerciseType) {
            if (exerciseType != null) {
                initialize()
            }
        }
        LaunchedEffect(hasPermission) {
            if (!hasPermission) {
                requestPermission()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (hasPermission) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = exerciseType!!.type,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.workout_label_count_format, uiState.count),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(
                            R.string.workout_label_status_format,
                            uiState.userPose
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = onStopClick,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = stringResource(R.string.workout_button_stop))
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (isPreview) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("카메라 미리보기 영역", color = Color.White, fontSize = 18.sp)
                    }
                } else {
                    CameraView(
                        modifier = Modifier.fillMaxSize(),
                        lifecycleOwner = lifecycleOwner,
                        imageAnalysisUseCase = imageAnalysisUseCase
                    )
                }
                val detectedPose = uiState.detectedPose
                if (detectedPose != null) {
                    PoseOverlay(
                        pose = detectedPose,
                        modifier = Modifier.fillMaxSize(),
                        imageWidth = 480,
                        imageHeight = 640
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.permission_camera_request))
            }
        }
    }
}

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner,
    imageAnalysisUseCase: UseCase
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
                        imageAnalysisUseCase as ImageAnalysis
                    )
                } catch (e: Exception) {
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@androidx.compose.ui.tooling.preview.Preview
fun WorkOutScreenPreview() {
    val fakeExerciseUiState = ExerciseUiState(count = 10)
    CarefullTheme {
        WorkOutScreen(
            imageAnalysisUseCase = ImageAnalysis.Builder().build(),
            hasPermission = true,
            requestPermission = {},
            uiState = fakeExerciseUiState,
            exerciseType = ExerciseType.DUMBBELL_CURL,
            onStopClick = {},
            initialize = {},
            isPreview = true
        )
    }
}
