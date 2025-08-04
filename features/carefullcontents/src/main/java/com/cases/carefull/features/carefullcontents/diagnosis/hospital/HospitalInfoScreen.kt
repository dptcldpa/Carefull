package com.cases.carefull.features.carefullcontents.diagnosis.hospital

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView

@Composable
fun HospitalInfoScreen(
    department: String,
    diagnosis: String
) {

    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            getMapAsync { naverMap ->
                naverMap.cameraPosition = CameraPosition(
                    LatLng(37.5665, 126.9780),
                    14.0
                )
            }
        }
    }

    var showBestHospitals by remember { mutableStateOf(true) }
    var showAllHospitals by remember { mutableStateOf(false) }

    var selectedHospitalType by remember { mutableStateOf("우수병원") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "예상 병명",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = diagnosis, //  챗봇에서 전달된 병명
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "진료 과목",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = department, //  챗봇에서 전달된 진료과
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showBestHospitals = !showBestHospitals
                    if (showBestHospitals) showAllHospitals = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (showBestHospitals) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
            Text(
                text = "우수 병원",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (showBestHospitals) {
            Spacer(modifier = Modifier.height(8.dp))
            // 여기에 우수 병원 리스트 넣기
            Text("서울아산병원")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showAllHospitals = !showAllHospitals
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (showAllHospitals) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
            Text(
                text = "전체 병원",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (showAllHospitals) {
            Spacer(modifier = Modifier.height(8.dp))
            // 여기에 전체 병원 리스트 넣기
            Text("한림대학교성심병원")
        }
    }
}