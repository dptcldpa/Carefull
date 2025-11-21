package com.cases.carefull.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cases.carefull.common.MainViewModel
import com.cases.carefull.features.carefullcommon.navigation.LayoutAsset
import com.cases.carefull.features.carefullcommon.navigation.MainRoute
import com.cases.carefull.features.carefullcommon.navigation.Route
import com.cases.carefull.features.carefullcontents.diagnosis.hospital.HospitalViewModel
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineViewModel
import com.cases.carefull.features.carefullcontents.navigation.diagnosisGraph
import com.cases.carefull.features.carefullcontents.navigation.feedGraph
import com.cases.carefull.features.carefullcontents.navigation.routineGraph
import com.cases.carefull.features.carefullmainui.navigation.mainGraph
import com.cases.carefull.features.carefullmainui.navigation.myPageGraph

@Composable
fun MainNavigation(
	viewModel: MainViewModel = hiltViewModel(),
	medicineViewModel: MedicineViewModel = hiltViewModel(),
	hospitalViewModel: HospitalViewModel = hiltViewModel()
) {
	val navController = rememberNavController()
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute: Route? by remember(navBackStackEntry) {
		derivedStateOf {
			val routeString = navBackStackEntry?.destination?.route
			LayoutAsset.findRouteByString(routeString)
		}
	}
	LaunchedEffect(navBackStackEntry) {
		viewModel.onRouteChanged(currentRoute)
	}
	MainScaffold(
		viewModel = viewModel,
		navController = navController
	) { innerPadding ->
		NavHost(
			navController = navController,
			startDestination = MainRoute.Splash,
			modifier = Modifier.fillMaxSize()
		) {
			mainGraph(navController)
			
			routineGraph(navController)
			
			diagnosisGraph(medicineViewModel = medicineViewModel,
				hospitalViewModel = hospitalViewModel,
				navController)
			
			feedGraph(navController)
			
			myPageGraph(navController)
		}
	}
}