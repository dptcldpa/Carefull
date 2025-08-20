package com.cases.carefull.features.carefullcommon.components

import com.cases.carefull.domain.model.NavType
import com.cases.carefull.domain.model.ScreenConfig
import com.cases.carefull.features.carefullcommon.model.NavItem
import com.cases.carefull.features.carefullcommon.navigation.DiagnosisRoute
import com.cases.carefull.features.carefullcommon.navigation.FeedRoute
import com.cases.carefull.features.carefullcommon.navigation.MainRoute
import com.cases.carefull.features.carefullcommon.navigation.MyPageRoute
import com.cases.carefull.features.carefullcommon.navigation.Route
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute

object LayoutAsset {
	private val navItemConfig = mapOf(
		NavType.TOP_ROUTINE to listOf(
			NavItem("운동", RoutineRoute.ExerciseScreen),
			NavItem("식단", RoutineRoute.DietScreen)
		),
		NavType.TOP_DIAGNOSIS to listOf(
			NavItem("진료", DiagnosisRoute.ChatBotScreen),
			NavItem("검색", DiagnosisRoute.HospitalSearchScreen)
		),
		NavType.TOP_FEED to listOf(
			NavItem("소셜", FeedRoute.Social),
			NavItem("랭킹", FeedRoute.RankingScreen)
		),
		NavType.NONE to emptyList(),
		
		NavType.SUB_DIAGNOSIS to listOf(
			NavItem("챗봇", DiagnosisRoute.ChatBotScreen),
			NavItem("병원 정보", DiagnosisRoute.HospitalInfoScreen),
			NavItem("약 정보", DiagnosisRoute.MedicineInfoScreen)
		),
		NavType.SUB_SEARCH to listOf(
			NavItem("병원", DiagnosisRoute.HospitalSearchScreen),
			NavItem("질환", DiagnosisRoute.DiseaseSearchScreen),
			NavItem("약", DiagnosisRoute.MedicineSearchScreen)
		),
		NavType.BOTTOM_MAIN to listOf(
			NavItem("홈", MainRoute.Home, "home"),
			NavItem("루틴", RoutineRoute.ExerciseScreen, "routine"),
			NavItem("진단", DiagnosisRoute.ChatBotScreen, "diagnosis"),
			NavItem("피드", FeedRoute.Social, "feed"),
			NavItem("마이페이지", MyPageRoute.MyPage, "mypage")
		)
	)
	private val screenConfig = mapOf(
		MainRoute.Home to ScreenConfig(
			showBottomBar = true),
		MyPageRoute.MyPage to ScreenConfig(
			showBottomBar = true),
		
		//루틴
		RoutineRoute.ExerciseScreen to ScreenConfig(
			topBarType = NavType.TOP_ROUTINE,
			showBottomBar = true
		),
		RoutineRoute.DietScreen to ScreenConfig(
			topBarType = NavType.TOP_ROUTINE,
			showBottomBar = true),
		RoutineRoute.DietSearchScreen to ScreenConfig(
			topBarType = NavType.TOP_ROUTINE,
			showBottomBar = true
		),
		RoutineRoute.FoodInformation to ScreenConfig(
			topBarType = NavType.TOP_ROUTINE,
			showBottomBar = true
		),
		RoutineRoute.WorkOutScreen to ScreenConfig(
			topBarType = NavType.NONE,
			showBottomBar = false
		),
		//진단
		DiagnosisRoute.ChatBotScreen to ScreenConfig(
			topBarType = NavType.TOP_DIAGNOSIS,
			subTopBarType = NavType.SUB_DIAGNOSIS,
			showBottomBar = true
		),
		DiagnosisRoute.HospitalInfoScreen to ScreenConfig(
			topBarType = NavType.TOP_DIAGNOSIS,
			subTopBarType = NavType.SUB_DIAGNOSIS,
			showBottomBar = true
		),
		DiagnosisRoute.MedicineInfoScreen to ScreenConfig(
			topBarType = NavType.TOP_DIAGNOSIS,
			subTopBarType = NavType.SUB_DIAGNOSIS,
			showBottomBar = true
		),
		DiagnosisRoute.HospitalSearchScreen to ScreenConfig(
			topBarType = NavType.TOP_DIAGNOSIS,
			subTopBarType = NavType.SUB_SEARCH,
			showBottomBar = true
		),
		DiagnosisRoute.DiseaseSearchScreen to ScreenConfig(
			topBarType = NavType.TOP_DIAGNOSIS,
			subTopBarType = NavType.SUB_SEARCH,
			showBottomBar = true
		),
		DiagnosisRoute.MedicineSearchScreen to ScreenConfig(
			topBarType = NavType.TOP_DIAGNOSIS,
			subTopBarType = NavType.SUB_SEARCH,
			showBottomBar = true
		),
		
		//피드
		FeedRoute.Social to ScreenConfig(
			topBarType = NavType.TOP_FEED,
			showBottomBar = true),
		FeedRoute.RankingScreen to ScreenConfig(
			topBarType = NavType.TOP_FEED,
			showBottomBar = true),
		
		MyPageRoute.AccountManagement to ScreenConfig()
	)
	
	val allRoutes: List<Route> by lazy {
		MainRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				RoutineRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				DiagnosisRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				FeedRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				MyPageRoute::class.sealedSubclasses.mapNotNull { it.objectInstance }
	}
	
	private val routeMap: Map<String, Route> by lazy {
		allRoutes.associateBy { it::class.qualifiedName!! }
	}
	
	fun findRouteByString(routeString: String?): Route? {
		if (routeString == null) return null
		return routeMap[routeString]
	}
	
	fun getNavItems(type: NavType) = navItemConfig[type] ?: emptyList()
	fun getScreenConfig(route: Route) = screenConfig[route]
}