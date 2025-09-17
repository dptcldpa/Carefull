package com.cases.carefull.features.carefullcontents.navigation


import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.cases.carefull.features.carefullcommon.navigation.DiagnosisRoute
import com.cases.carefull.features.carefullcontents.diagnosis.chatbot.ChatBotScreen
import com.cases.carefull.features.carefullcontents.diagnosis.disease.DiseaseSearchScreen
import com.cases.carefull.features.carefullcontents.diagnosis.hospital.HospitalInfoScreen
import com.cases.carefull.features.carefullcontents.diagnosis.hospital.HospitalSearchScreen
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineInfoScreen
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineSearchScreen
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineViewModel

fun NavGraphBuilder.diagnosisGraph(
	viewModel: MedicineViewModel, navController: NavHostController
) {
	composable<DiagnosisRoute.ChatBotScreen> {
		ChatBotScreen(
			navController = navController
		)
	}

//								department, diagnosis ->
//				val encodedDept = Uri.encode(department)
//				val encodedDiag = Uri.encode(diagnosis)
//				navController.navigate("hospital_info/$encodedDept/$encodedDiag")
//						navController.navigate(DiagnosisRoute.HospitalInfoScreen)

	
	// 진료 - 병원
	composable<DiagnosisRoute.HospitalInfoScreen> {
		val args = it.arguments
		val department = args?.getString("department") ?: ""
		val diagnosis = args?.getString("diagnosis") ?: ""
		
		HospitalInfoScreen(
			department = department,
			diagnosis = diagnosis
		)
	}
	// 진료 - 약
	composable<DiagnosisRoute.MedicineInfoScreen> {
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()
		
		uiState.selectedItem?.let { item ->
			MedicineInfoScreen(
				medicineItem = item,
				navController = navController
			)
		}
	}
	// 검색 - 병원
	composable<DiagnosisRoute.HospitalSearchScreen> {
		HospitalSearchScreen()
	}
	// 검색 - 질환
	composable<DiagnosisRoute.DiseaseSearchScreen> {
		DiseaseSearchScreen()
	}
	// 검색 - 약
	composable<DiagnosisRoute.MedicineSearchScreen> {
		MedicineSearchScreen(
			viewModel = viewModel,
			onNavigateToMedicineInfo = {
				navController.navigate(DiagnosisRoute.MedicineInfoScreen)
			}
		)
	}
}