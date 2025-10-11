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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                getCurrentLocation(context, useFineAccuracy = true) { lat, lon ->
                    currentLocation = LatLng(lat, lon)
                    viewModel.searchHospitals(latitude = lat, longitude = lon)
                }
            }
            else if (permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                // 대략적인 위치 권한만 있으므로, 낮은 정확도로 위치 요청
                getCurrentLocation(context, useFineAccuracy = false) { lat, lon ->
                    currentLocation = LatLng(lat, lon)
                    viewModel.searchHospitals(latitude = lat, longitude = lon)
                }
            } else {
                // 어떤 위치 권한도 허용되지 않음
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
                Log.d("MapDebug", "--- NaverMap 객체 준비 완료 ---") // <--- 이 로그 추가
                naverMap = map
                map.cameraPosition = CameraPosition(
                    LatLng(37.5665, 126.9780),
                    14.0
                )
                map.uiSettings.isZoomControlEnabled = false
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

    LaunchedEffect(currentLocation, naverMap) {
        naverMap?.let { map ->
            currentLocation?.let { location ->
                val cameraUpdate = CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing)
                map.moveCamera(cameraUpdate)

                val locationOverlay = map.locationOverlay
                locationOverlay.isVisible = true
                locationOverlay.position = location
            }
        }
    }

    // 검색 결과(hospitals)가 변경될 때마다 마커를 새로 그립니다.
    val markers = remember { mutableListOf<Marker>() }
    LaunchedEffect(uiState.hospitals, naverMap) {
        naverMap?.let { map ->
            markers.forEach { it.map = null }
            markers.clear()

            uiState.hospitals.forEach { hospital ->
                try {
                    val lat = hospital.YPos?.toDoubleOrNull()
                    val lon = hospital.XPos?.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val newMarker = Marker().apply {
                            position = LatLng(lat, lon)
                            captionText = hospital.yadmNm
                            this.map = map
                        }
                        markers.add(newMarker)
                    }
                } catch (e: Exception) {
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
                onValueChange = viewModel::onSearchQueryChanged, // 2. 람다 대신 함수 참조 사용
                placeholder = { Text("장소를 입력하세요", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) // 화면 가장자리에 여백 추가
                    .clip(RoundedCornerShape(24.dp)) // 네이버 지도 스타일: 둥근 직사각형
                    .background(Color.White)
                    .padding(horizontal = 4.dp), // 아이콘과 텍스트가 너무 붙지 않게
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,        // 기본 배경색 투명화
                    focusedIndicatorColor = Color.Transparent, // 포커스 시 밑줄 제거
                    unfocusedIndicatorColor = Color.Transparent, // 비포커스 시 밑줄 제거
                    focusedContainerColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        currentLocation?.let { loc ->
                            viewModel.searchHospitals(loc.latitude, loc.longitude)
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

//            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
//            val sheetState = rememberModalBottomSheetState()
//            // 검색 결과 BottomSheet
//            if (uiState.hospitals.isNotEmpty() && !uiState.isLoading) {
//                ModalBottomSheet(
//                    onDismissRequest = { viewModel.clearHospitalSelection() },
//                    sheetState = sheetState,
//                    dragHandle = { BottomSheetDefaults.DragHandle() }
//                ) {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp)
//                    ) {
//                        items(uiState.hospitals, key = { it.ykiho }) { hospital ->
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable {
//                                        viewModel.selectHospital(hospital)
//                                        try {
//                                            val lat = hospital.YPos?.toDoubleOrNull()
//                                            val lon = hospital.XPos?.toDoubleOrNull()
//                                            if (lat != null && lon != null) {
//                                                val cameraUpdate = CameraUpdate
//                                                    .scrollTo(LatLng(lat, lon))
//                                                    .animate(CameraAnimation.Easing)
//                                                naverMap?.moveCamera(cameraUpdate)
//                                            }
//                                        } catch (e: Exception) {
//                                                    // Log.e("MapCamera", "좌표 변환 실패: $latStr, $lonStr")
//                                        }
//                                    }
//                                    .padding(horizontal = 24.dp, vertical = 16.dp)
//                            ) {
//                                Text(hospital.yadmNm, style = MaterialTheme.typography.titleMedium)
//                                Spacer(modifier = Modifier.height(4.dp))
//                                Text(hospital.addr, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}

// 현재 위치
@SuppressLint("MissingPermission")
private fun getCurrentLocation(context: Context, useFineAccuracy: Boolean, onResult: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val priority = if (useFineAccuracy) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY

    try {
        fusedLocationClient.getCurrentLocation(priority, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                location?.let {
                    onResult(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener { exception ->
            }
    } catch (e: SecurityException) {

    }
}
