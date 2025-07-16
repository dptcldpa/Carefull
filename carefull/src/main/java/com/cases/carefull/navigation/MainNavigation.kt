package com.cases.carefull.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cases.carefull.Splash
import com.cases.carefull.common.MainViewModel
import com.cases.carefull.di.MainViewModelFactory
import com.cases.carefull.features.carefullcommon.components.LayoutAsset
import com.cases.carefull.features.carefullcommon.components.NavigationRepositoryImpl
import com.cases.carefull.features.carefullcommon.navigation.DiagnosisRoute
import com.cases.carefull.features.carefullcommon.navigation.FeedRoute
import com.cases.carefull.features.carefullcommon.navigation.MainRoute
import com.cases.carefull.features.carefullcommon.navigation.MyPageRoute
import com.cases.carefull.features.carefullcommon.navigation.Route
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullcontents.diagnosis.chatbot.ChatBotScreen
import com.cases.carefull.features.carefullcontents.diagnosis.disease.DiseaseSearchScreen
import com.cases.carefull.features.carefullcontents.diagnosis.hospitalInfo.HospitalInfoScreen
import com.cases.carefull.features.carefullcontents.diagnosis.medicineInfo.MedicineInfoScreen
import com.cases.carefull.features.carefullcontents.feed.Ranking
import com.cases.carefull.features.carefullcontents.feed.Social
import com.cases.carefull.features.carefullcontents.routine.Diet
import com.cases.carefull.features.carefullcontents.routine.Exercise
import com.cases.carefull.features.carefullcontents.routine.FoodInformation
import com.cases.carefull.features.carefullcontents.routine.SearchFood
import com.cases.carefull.features.carefullmainui.screen.Home
import com.cases.carefull.features.carefullmainui.screen.auth.Signin
import com.cases.carefull.features.carefullmainui.screen.mypage.AccountManagement
import com.cases.carefull.features.carefullmainui.screen.mypage.BasalMetabolicRateMeasurement
import com.cases.carefull.features.carefullmainui.screen.mypage.MyPage
import com.cases.carefull.features.carefullmainui.screen.mypage.PostWrittenManagement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainNavigation() {
	val repository = remember { NavigationRepositoryImpl() }
	val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))
	val navController = rememberNavController()
	
	val uiState by viewModel.uiState.collectAsState()
	
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
	
	val context = LocalContext.current
	val activity = (context as? Activity)
	val scope = rememberCoroutineScope()
	var backPressedOnce by remember { mutableStateOf(false) }
	
	BackHandler(enabled = true) {
		if (uiState.showBottomBar && currentRoute != null) {
			if (currentRoute !is MainRoute.Home) {
				navController.navigate(MainRoute.Home) {
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
			composable<MainRoute.Splash> {
				Splash(
					shift = {
						navController.navigate(MainRoute.Signin) {
							popUpTo(MainRoute.Splash) { inclusive = true }
						}
					}
				)
			}
			composable<MainRoute.Signin> {
				Signin(
					onLoginClick = {
						navController.navigate(MainRoute.Home) {
							popUpTo(MainRoute.Signin) {
								inclusive = true
							}
						}
					}
				)
			}
			composable<MainRoute.Home> {
				Home()
			}
			
			//루틴
			composable<RoutineRoute.Exercise> {
				Exercise()
			}
			composable<RoutineRoute.Diet> {
				Diet()
			}
			composable<RoutineRoute.SearchFood> {
				SearchFood()
			}
			composable<RoutineRoute.FoodInformation> {
				FoodInformation()
			}
			
			//진단
			composable<DiagnosisRoute.ChatBotScreen> {
				ChatBotScreen()
			}
			composable<DiagnosisRoute.DiseaseSearchScreen> {
				DiseaseSearchScreen()
			}
			composable<DiagnosisRoute.HospitalInfo> {
				HospitalInfoScreen()
			}
			composable<DiagnosisRoute.MedicineInfoScreen> {
				MedicineInfoScreen()
			}
			
			//피드
			composable<FeedRoute.Social> {
				Social()
			}
			composable<FeedRoute.Ranking> {
				Ranking()
			}
			
			//마이페이지
			composable<MyPageRoute.MyPage> {
				MyPage()
			}
			composable<MyPageRoute.AccountManagement> {
				AccountManagement()
			}
			composable<MyPageRoute.BasalMetabolicRateMeasurement> {
				BasalMetabolicRateMeasurement()
			}
			composable<MyPageRoute.PostWrittenManagement> {
				PostWrittenManagement()
			}
		}
	}
}