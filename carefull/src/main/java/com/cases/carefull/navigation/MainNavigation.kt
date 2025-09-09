package com.cases.carefull.navigation

import android.app.Activity
import android.net.Uri
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cases.carefull.features.carefullmainui.screen.Splash
import com.cases.carefull.common.CarefullApplication
import com.cases.carefull.common.MainViewModel
import com.cases.carefull.di.ViewModelFactory
import com.cases.carefull.features.carefullcommon.components.LayoutAsset
import com.cases.carefull.features.carefullcommon.navigation.DiagnosisRoute
import com.cases.carefull.features.carefullcommon.navigation.FeedRoute
import com.cases.carefull.features.carefullcommon.navigation.MainRoute
import com.cases.carefull.features.carefullcommon.navigation.MyPageRoute
import com.cases.carefull.features.carefullcommon.navigation.Route
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import com.cases.carefull.features.carefullcontents.diagnosis.chatbot.ChatBotScreen
import com.cases.carefull.features.carefullcontents.diagnosis.disease.DiseaseSearchScreen
import com.cases.carefull.features.carefullcontents.diagnosis.hospital.HospitalInfoScreen
import com.cases.carefull.features.carefullcontents.diagnosis.hospital.HospitalSearchScreen
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineInfoScreen
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineSearchScreen
import com.cases.carefull.features.carefullcontents.diagnosis.medicine.MedicineViewModel
import com.cases.carefull.features.carefullcontents.feed.RankingScreen
import com.cases.carefull.features.carefullcontents.feed.Social
import com.cases.carefull.features.carefullcontents.routine.diet.BmrScreen
import com.cases.carefull.features.carefullcontents.routine.diet.DietScreen
import com.cases.carefull.features.carefullcontents.routine.diet.DietSearchScreen
import com.cases.carefull.features.carefullcontents.routine.diet.FoodInformation
import com.cases.carefull.features.carefullcontents.routine.exercise.ExerciseScreen
import com.cases.carefull.features.carefullcontents.routine.exercise.WorkOutScreen
import com.cases.carefull.features.carefullmainui.home.HomeScreen
import com.cases.carefull.features.carefullmainui.screen.auth.OAuthViewModel
import com.cases.carefull.features.carefullmainui.screen.auth.SigninScreen
import com.cases.carefull.features.carefullmainui.screen.mypage.AccountManagement
import com.cases.carefull.features.carefullmainui.screen.mypage.MyPage
import com.cases.carefull.features.carefullmainui.screen.mypage.PostWrittenManagement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation (
	viewModel: MainViewModel = hiltViewModel()
) {
	val application = LocalContext.current.applicationContext as CarefullApplication
	val container = application.container
	
	val viewModelFactory = ViewModelFactory(
//		navigationRepository = container.navigationRepository,
		medicineSearchUseCase = container.medicineSearchUseCase,
//		dietRepository = container.dietRepository,
//		exerciseRepository = container.exerciseRepository,
		userRepository = container.userRepository,
//		rankingRepository = container.rankingRepository,
//		calendarRepository = container.calendarRepository
	)
	
	val medicineViewModel: MedicineViewModel = viewModel(factory = viewModelFactory)
//	val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
//	val dietViewModel: DietViewModel = viewModel(factory = viewModelFactory)
//	val exerciseViewModel: ExerciseViewModel = viewModel(factory = viewModelFactory)
	val oauthViewModel: OAuthViewModel = viewModel(factory = viewModelFactory)
//	val rankingViewModel: RankingViewModel = viewModel(factory = viewModelFactory)
//	val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
	
	val navController = rememberNavController()
	val uiState by viewModel.uiState.collectAsState()
	val loginUiState by oauthViewModel.uiState.collectAsStateWithLifecycle()
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
	
	LaunchedEffect(key1 = loginUiState.isLoading) {
		if (!loginUiState.isLoading) {
			delay(1000L)
			
			if (loginUiState.userInfo != null) {
				navController.navigate(MainRoute.HomeScreen) {
					popUpTo(MainRoute.Splash) { inclusive = true }
					launchSingleTop = true
				}
			} else if (loginUiState.errorMessage == null) {
				navController.navigate(MainRoute.SigninScreen) {
					popUpTo(MainRoute.Splash) { inclusive = true }
					launchSingleTop = true
				}
			}
		}
	}
	
	LaunchedEffect(Unit) {
		oauthViewModel.checkLoggedInState()
	}
	
	val context = LocalContext.current
	val activity = (context as? Activity)
	val scope = rememberCoroutineScope()
	var backPressedOnce by remember { mutableStateOf(false) }
	
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
			composable<MainRoute.Splash> {
				Splash(
					shift = {
						navController.navigate(MainRoute.SigninScreen) {
							popUpTo(MainRoute.Splash) { inclusive = true }
						}
					}
				)
			}
			composable<MainRoute.SigninScreen> {
				LaunchedEffect(key1 = loginUiState.errorMessage) {
					loginUiState.errorMessage?.let {
						Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
						oauthViewModel.errorMessageShown()
					}
				}
				SigninScreen(
					isLoading = loginUiState.isLoading,
					onLoginClick = {
						navController.navigate(MainRoute.HomeScreen) {
							popUpTo(MainRoute.SigninScreen) {
								inclusive = true
							}
						}
					},
					onKakaoLoginClick = { oauthViewModel.loginWithKakao() }
				)
			}
			
			composable<MainRoute.HomeScreen> {
				HomeScreen(
//					viewModel = homeViewModel,
					navController = navController)
			}
			//루틴
			composable<RoutineRoute.ExerciseScreen> { navBackStackEntry ->
				ExerciseScreen(
//					viewModel = exerciseViewModel,
					navController = navController
				)
			}
			composable<RoutineRoute.WorkOutScreen> { navBackStackEntry ->
				WorkOutScreen(
//					viewModel = exerciseViewModel,
					navController = navController
				)
			}
			composable<RoutineRoute.DietScreen> { navBackStackEntry ->
				DietScreen(
//					viewModel = dietViewModel,
					navController = navController
				)
			}
			composable<RoutineRoute.DietSearchScreen> { navBackStackEntry ->
				DietSearchScreen(
//					viewModel = dietViewModel,
					navController = navController,
				)
			}
			composable<RoutineRoute.BmrScreen> {
				BmrScreen(
//					viewModel = dietViewModel
				)
			}
			composable<RoutineRoute.FoodInformation> {
				FoodInformation()
			}
			
			//진단
			// 진료 - 챗봇
			composable<DiagnosisRoute.ChatBotScreen> {
				ChatBotScreen(
					onDepartmentClick = { department, diagnosis ->
						val encodedDept = Uri.encode(department)
						val encodedDiag = Uri.encode(diagnosis)
						navController.navigate("hospital_info/$encodedDept/$encodedDiag")
					}
//						navController.navigate(DiagnosisRoute.HospitalInfoScreen)
				)
			}
			
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
				val uiState by medicineViewModel.uiState.collectAsStateWithLifecycle()
				
				uiState.selectedItem?.let { item ->
					MedicineInfoScreen(medicineItem = item)
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
					viewModel = medicineViewModel,
					onNavigateToMedicineInfo = {
						navController.navigate(DiagnosisRoute.MedicineInfoScreen)
					}
				)
			}
			
			//피드
			composable<FeedRoute.Social> {
				Social()
			}
			composable<FeedRoute.RankingScreen> {
				RankingScreen(
//					viewModel = rankingViewModel
				)
			}
			
			//마이페이지
			composable<MyPageRoute.MyPage> {
				MyPage(navController = navController)
			}
			composable<MyPageRoute.AccountManagement> {
				AccountManagement()
			}
			composable<MyPageRoute.PostWrittenManagement> {
				PostWrittenManagement()
			}
		}
	}
}