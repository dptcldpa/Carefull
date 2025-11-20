package com.cases.carefull.features.carefullcontents.diagnosis.hospital
// 챗봇
import android.annotation.SuppressLint
import android.content.Context
//import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cases.carefull.domain.model.Hospital
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HospitalListScreen(
    viewModel: HospitalViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var naverMap by remember { mutableStateOf<NaverMap?>(null) }
    val mapView = rememberMapViewWithLifecycle(onMapReady = { map -> naverMap = map })


    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(naverMap, locationPermissionsState.allPermissionsGranted, uiState.department) {
        if (naverMap == null) return@LaunchedEffect

        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
            return@LaunchedEffect
        }

        if (uiState.department.isNotEmpty()) {
            getCurrentLocation(context) { lat, lon ->
                val currentLocation = LatLng(lat, lon)
                val cameraUpdate = CameraUpdate.scrollAndZoomTo(currentLocation, 14.0)
                naverMap?.moveCamera(cameraUpdate)

                viewModel.loadHospitals(lat, lon)
            }
        }
    }

    val markers = remember { mutableStateListOf<Marker>() }

    LaunchedEffect(uiState.allHospitals, naverMap) {
        Log.d("MarkerDebug", "Effect 실행됨. naverMap is null: ${naverMap == null}, 병원 수: ${uiState.allHospitals.size}")

        naverMap?.let { map ->
            markers.forEach { it.map = null }
            markers.clear()

            uiState.allHospitals.forEach { hospital ->
                Log.d("MarkerDebug", "naverMap?.let 블록 실행됨. 마커 생성 시작.")
                Log.d("MarkerDebug", "병원: ${hospital.name}, Lat: ${hospital.YPos}, Lon: ${hospital.XPos}")

                val lat = hospital.XPos
                val lon = hospital.YPos

                if (lat != null && lon != null) {
                    Log.d("MarkerDebug", "마커 생성: ${hospital.name} at ($lat, $lon)")

                    val marker = Marker().apply {
                        position = LatLng(lat, lon)
                        captionText = hospital.name

                        if (hospital.isExcellent) {
                            captionText = "⭐ ${hospital.name}"
                            iconTintColor = 0xFFFF0000.toInt()
                            width = 70
                            height = 90
                        } else {
                            captionText = hospital.name
                            iconTintColor = 0xFF1976D2.toInt()
                            width = 60
                            height = 80
                        }

                        this.map = map

                    }
                    markers.add(marker)
                } else {
                    Log.w("MarkerDebug", "좌표가 null인 병원: ${hospital.name}")
                }
            }
        }
    }

    LaunchedEffect(uiState.selectedHospital) {
        uiState.selectedHospital?.let { hospital ->
            val lat = hospital.XPos
            val lon = hospital.YPos
            if (lat != null && lon != null) {
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(lat, lon))
                    .animate(CameraAnimation.Easing)
                naverMap?.moveCamera(cameraUpdate)
            }
        }
    }

    var showBestHospitals by remember { mutableStateOf(true) }
    var showAllHospitals by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "'${uiState.department}' 병원 목록") })
        }
    ) {
        innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null && uiState.allHospitals.isEmpty()) {
                Text(
                    text = uiState.errorMessage!!,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 지도
                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // 지도
                            AndroidView(
                                factory = { mapView },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                update = { view ->
                                    view.setOnTouchListener { v, event ->
                                        when (event.action) {
                                            MotionEvent.ACTION_DOWN -> {
                                                v.parent.requestDisallowInterceptTouchEvent(true)
                                            }

                                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                                v.parent.requestDisallowInterceptTouchEvent(false)
                                            }
                                        }
                                        false
                                    }
                                }
                            )
                            Card(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.9f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(Color.Red, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "우수",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(Color.Blue, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "일반",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            FloatingActionButton(
                                onClick = {
                                    getCurrentLocation(context) { lat, lon ->
                                        val currentLocation = LatLng(lat, lon)
                                        val cameraUpdate = CameraUpdate.scrollAndZoomTo(currentLocation, 15.0)
                                            .animate(CameraAnimation.Easing)
                                        naverMap?.moveCamera(cameraUpdate)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp),
                                containerColor = Color.White,
                                contentColor = Color.Blue
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "현재 위치로 이동"
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showBestHospitals = !showBestHospitals },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (showBestHospitals) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("우수 병원", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (showBestHospitals) {
                        if (uiState.bestHospitals.isNotEmpty()) {
                            items(uiState.bestHospitals, key = { it.id }) { hospital ->
                                HospitalItem(hospital = hospital, onClick = { viewModel.selectHospital(hospital) })
                            }
                        } else {
                            item { Text("해당하는 우수 병원 정보가 없습니다.", modifier = Modifier.padding(start = 24.dp)) }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showAllHospitals = !showAllHospitals },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (showAllHospitals) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("전체 병원", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (showAllHospitals) {
                        if (uiState.allHospitals.isNotEmpty()) {
                            items(uiState.allHospitals, key = { it.id }) { hospital ->
                                HospitalItem(hospital = hospital, onClick = { viewModel.selectHospital(hospital) })
                            }
                        } else {
                            item { Text("해당하는 병원 정보가 없습니다.", modifier = Modifier.padding(start = 24.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(onMapReady: (NaverMap) -> Unit): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }

        mapView.getMapAsync(onMapReady)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    return mapView
}

@Composable
private fun HospitalItem(
    hospital: Hospital,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = hospital.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (hospital.isExcellent) {

                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = hospital.address,
                style = MaterialTheme.typography.bodyMedium
            )

            hospital.distance?.let { distance ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "거리: %.1fkm".format(distance),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
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
