package com.cases.carefull.features.carefullcontents.diagnosis.hospital

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker

@Composable
@ExperimentalMaterial3Api
fun HospitalSearchScreen(
    viewModel: HospitalViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

//    val markers = remember { mutableListOf<Marker>() }
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    var initialLocationLoaded by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

            if (isGranted) {
                getCurrentLocation(context) { lat, lon ->
                    if (!initialLocationLoaded) {
                        val currentLocation = LatLng(lat, lon)
                        val cameraUpdate = CameraUpdate.scrollTo(currentLocation)
                        naverMap?.moveCamera(cameraUpdate)
                        viewModel.onCameraMoved(lat, lon)

                        val locationOverlay = naverMap?.locationOverlay
                        locationOverlay?.isVisible = true
                        locationOverlay?.position = currentLocation

                        initialLocationLoaded = true
                    }
                }
            } else {
                viewModel.onCameraMoved(37.5665, 126.9780)
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    val mapView = remember {
        MapView(context).apply {
            getMapAsync { map ->
                Log.d("MapDebug", "--- NaverMap 객체 준비 완료 ---")
                naverMap = map
                map.cameraPosition = CameraPosition(
                    LatLng(37.5665, 126.9780),
                    14.0
                )
                map.addOnCameraIdleListener {
                    val center = map.cameraPosition.target
                    viewModel.onCameraMoved(center.latitude, center.longitude)
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val markers = remember { mutableListOf<Marker>() }
    LaunchedEffect(uiState.searchHospitals, naverMap) {
        naverMap?.let { map ->
            markers.forEach { it.map = null }
            markers.clear()

            uiState.searchHospitals.forEach { hospital ->
                try {
                    val lat = hospital.YPos
                    val lon = hospital.XPos
                    if (lat != null && lon != null) {
                        val newMarker = Marker().apply {
                            position = LatLng(lat, lon)
                            captionText = hospital.name
                            this.map = map
                        }
                        markers.add(newMarker)
                    }
                } catch (e: Exception) {
                    Log.e("MarkerCreation", "Failed to create marker for ${hospital.name}", e)
                }
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.errorMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 지도
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

            // 검색창
            TextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("장소를 입력하세요", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(horizontal = 4.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(onClick = {

                        naverMap?.cameraPosition?.target?.let { center ->
                            viewModel.searchHospitals(center.latitude, center.longitude)
                        }
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색",
                            tint = Color.Gray
                        )
                    }
                }
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

            if (uiState.searchHospitals.isNotEmpty()) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.clearHospitalSelection() },
                    sheetState = sheetState
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        items(uiState.searchHospitals, key = { it.id }) { hospital ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectHospital(hospital)
                                        try {
                                            val lat = hospital.YPos
                                            val lon = hospital.XPos
                                            if (lat != null && lon != null) {
                                                val cameraUpdate = CameraUpdate
                                                    .scrollTo(LatLng(lat, lon))
                                                    .animate(CameraAnimation.Easing)
                                                naverMap?.moveCamera(cameraUpdate)
                                            }
                                        } catch (e: Exception) {

                                        }
                                    }
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            ) {
                                Text(hospital.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(hospital.address, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 현재 위치
@SuppressLint("MissingPermission")
private fun getCurrentLocation(context: Context, onResult: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                location?.let {
                    onResult(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener {
                Log.w("Location", "Failed to get high accuracy location, trying balanced.")
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
                    .addOnSuccessListener { location ->
                        location?.let {
                            onResult(it.latitude, it.longitude)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Location", "Failed to get any location.", e)
                    }
            }
    } catch (e: SecurityException) {
        Log.e("Location", "Location permission not granted.", e)
    }
}
