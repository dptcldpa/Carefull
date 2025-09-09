package com.cases.carefull.navigation

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cases.carefull.common.MainViewModel
import com.cases.carefull.features.carefullcommon.components.LayoutAsset
import com.cases.carefull.features.carefullcommon.navigation.MainRoute
import com.cases.carefull.features.carefullcommon.navigation.Route
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineViewModel
import com.cases.carefull.features.carefullcontents.navigation.diagnosisGraph
import com.cases.carefull.features.carefullcontents.navigation.feedGraph
import com.cases.carefull.features.carefullcontents.navigation.routineGraph
import com.cases.carefull.features.carefullmainui.navigation.mainGraph
import com.cases.carefull.features.carefullmainui.navigation.myPageGraph
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation(
	viewModel: MainViewModel = hiltViewModel(),
	medicineViewModel: MedicineViewModel = hiltViewModel()
) {
	val navController = rememberNavController()
	val uiState by viewModel.uiState.collectAsState()
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	
	val currentRoute: Route? by remember(navBackStackEntry) {
		derivedStateOf {
			val routeString = navBackStackEntry?.destination?.route
			LayoutAsset.findRouteByString(routeString)
		}
	}
	
	val context = LocalContext.current
	val activity = (context as? Activity)
	val scope = rememberCoroutineScope()
	var backPressedOnce by remember { mutableStateOf(false) }
	
	LaunchedEffect(navBackStackEntry) {
		viewModel.onRouteChanged(currentRoute)
	}
	
	BackHandler(enabled = true) {
		if (uiState.showBottomBar && currentRoute != null) {
			if (currentRoute !is MainRoute.HomeScreen) {
				navController.navigate(MainRoute.HomeScreen) {
					popUpTo(navController.graph.findStartDestination().id) { saveState = true }
					launchSingleTop = true
					restoreState = true
				}
			} else {
				if (backPressedOnce) {
					activity?.finish()
				} else {
					backPressedOnce = true
					Toast.makeText(context, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
					scope.launch { delay(2000L); backPressedOnce = false }
				}
			}
		} else {
			navController.popBackStack()
		}
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
			
			diagnosisGraph(viewModel = medicineViewModel, navController)
			
			feedGraph(navController)
			
			myPageGraph(navController)
		}
	}
}