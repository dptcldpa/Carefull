package com.cases.carefull.features.carefullcontents.diagnosis.hospital

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var naverMap by remember { mutableStateOf<NaverMap?>(null) }

    var initialLocationLoaded by remember { mutableStateOf(false) }

    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

            if (isGranted) {
                viewModel.loadCurrentLocationForSearch { location ->
                    if (!initialLocationLoaded) {
                        val currentLocation = LatLng(location.latitude, location.longitude)
                        val cameraUpdate = CameraUpdate.scrollTo(currentLocation)
                        naverMap?.moveCamera(cameraUpdate)
                        viewModel.onCameraMoved(location.latitude, location.longitude)

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
    LaunchedEffect(uiState.filteredHospitals, naverMap) {
        naverMap?.let { map ->
            markers.forEach { it.map = null }
            markers.clear()

            uiState.filteredHospitals.forEach { hospital ->
                try {
                    val lat = hospital.XPos
                    val lon = hospital.YPos
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

    val sheetPeekHeight = if (uiState.filteredHospitals.isNotEmpty()) 28.dp else 0.dp

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContent = {
            if (uiState.filteredHospitals.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    ) {
                        items(uiState.filteredHospitals, key = { it.id }) { hospital ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectHospital(hospital)
                                        try {
                                            val lat = hospital.XPos
                                            val lon = hospital.YPos
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
                                Text(hospital.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(hospital.address, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                thickness = 0.5.dp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(1.dp))
            }
        },
        sheetPeekHeight = sheetPeekHeight,
        sheetDragHandle = {
            if (uiState.filteredHospitals.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(Color.Gray, RoundedCornerShape(2.dp))
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 지도
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                // 검색창
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = { Text("병원명을 입력해주세요", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
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

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedDepartment == null,
                            onClick = { viewModel.selectDepartment(null) },
                            label = { Text("전체") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White,
                            ),
                            border = null
                        )
                    }

                    items(viewModel.departmentList) { department ->
                        FilterChip(
                            selected = uiState.selectedDepartment == department.name,
                            onClick = { viewModel.selectDepartment(department.name) },
                            label = { Text(department.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White,
                            ),
                            border = null
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    viewModel.loadCurrentLocationForSearch { location ->
                        val currentLocation = LatLng(location.latitude, location.longitude)
                        val cameraUpdate = CameraUpdate
                            .scrollAndZoomTo(currentLocation, 15.0)
                            .animate(CameraAnimation.Easing)
                        naverMap?.moveCamera(cameraUpdate)

                        val locationOverlay = naverMap?.locationOverlay
                        locationOverlay?.isVisible = true
                        locationOverlay?.position = currentLocation
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 100.dp),
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "현재 위치로 이동"
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    LaunchedEffect(uiState.filteredHospitals) {
        if (uiState.filteredHospitals.isNotEmpty()) {
            bottomSheetState.bottomSheetState.expand()
        }
    }
}
