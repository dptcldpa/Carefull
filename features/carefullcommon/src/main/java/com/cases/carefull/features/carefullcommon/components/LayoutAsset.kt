package com.cases.carefull.features.carefullcommon.components

import com.cases.carefull.domain.model.NavType
import com.cases.carefull.domain.model.ScreenConfig
import com.cases.carefull.domain.model.exercise.ExerciseType
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
			NavItem("식단", RoutineRoute.DietRoute)
		),
		NavType.TOP_DIAGNOSIS to listOf(
			NavItem("진료", DiagnosisRoute.ChatBotScreen),
			NavItem("검색", DiagnosisRoute.HospitalSearchScreen)
		),
		NavType.TOP_FEED to listOf(
			NavItem("소셜", FeedRoute.SocialListScreen),
			NavItem("랭킹", FeedRoute.RankingScreen)
		),
		NavType.NONE to emptyList(),
		
		NavType.SUB_DIAGNOSIS to listOf(
			NavItem("챗봇", DiagnosisRoute.ChatBotScreen),
			NavItem("병원 정보", DiagnosisRoute.HospitalSearchScreen),
			NavItem("약 정보", DiagnosisRoute.MedicineSearchScreen)
		),
		NavType.SUB_SEARCH to listOf(
			NavItem("병원", DiagnosisRoute.HospitalSearchScreen),
			NavItem("질환", DiagnosisRoute.DiseaseSearchScreen),
			NavItem("약", DiagnosisRoute.MedicineSearchScreen)
		),
		NavType.BOTTOM_MAIN to listOf(
			NavItem("홈", MainRoute.HomeScreen, "home"),
			NavItem("루틴", RoutineRoute.ExerciseScreen, "routine"),
			NavItem("진단", DiagnosisRoute.ChatBotScreen, "diagnosis"),
			NavItem("피드", FeedRoute.SocialListScreen, "feed"),
			NavItem("마이", MyPageRoute.MyPage, "mypage")
		)
	)
	private val screenConfig = mapOf(
		MainRoute.HomeScreen to ScreenConfig(
			showBottomBar = true
		),
		MyPageRoute.MyPage to ScreenConfig(
			showBottomBar = true
		),
		
		//루틴
		RoutineRoute.ExerciseScreen to ScreenConfig(
			topBarType = NavType.TOP_ROUTINE,
			showBottomBar = true
		),
		RoutineRoute.DietRoute to ScreenConfig(
			topBarType = NavType.TOP_ROUTINE,
			subTopBarType = NavType.NONE,
			showBottomBar = true
		),
		RoutineRoute.DietSearchScreen to ScreenConfig(
			topBarType = NavType.TOP_ROUTINE,
			subTopBarType = NavType.NONE,
			showBottomBar = true
		),
		RoutineRoute.WorkOutScreen to ScreenConfig(
			topBarType = NavType.NONE,
			subTopBarType = NavType.NONE,
			showBottomBar = false
		),
		RoutineRoute.BmrRoute to ScreenConfig(
			topBarType = NavType.NONE,
			showBottomBar = false
		),
		//진단
		DiagnosisRoute.ChatBotScreen to ScreenConfig(
			topBarType = NavType.TOP_DIAGNOSIS,
			subTopBarType = NavType.NONE,
			showBottomBar = true
		),
		DiagnosisRoute.HospitalListScreen to ScreenConfig(
			topBarType = NavType.TOP_DIAGNOSIS,
			subTopBarType = NavType.SUB_DIAGNOSIS,
			showBottomBar = true
		),
		DiagnosisRoute.MedicineInfoScreen to ScreenConfig(
			topBarType = NavType.NONE,
			subTopBarType = NavType.NONE,
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
		FeedRoute.SocialListScreen to ScreenConfig(
			topBarType = NavType.TOP_FEED,
			showBottomBar = true
		),
		FeedRoute.CreatePostScreen to ScreenConfig(
			topBarType = NavType.NONE,
			subTopBarType = NavType.NONE,
			showBottomBar = false
		),
		FeedRoute.PostDetailScreen(postId = "") to ScreenConfig(
			topBarType = NavType.NONE,
			subTopBarType = NavType.NONE,
			showBottomBar = false
		),
		FeedRoute.RankingScreen to ScreenConfig(
			topBarType = NavType.TOP_FEED,
			subTopBarType = NavType.NONE,
			showBottomBar = true
		),
		
		MyPageRoute.AccountManagement to ScreenConfig()
	)
	
	val allRoutes: List<Route> by lazy {
		MainRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				RoutineRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				listOf(
					RoutineRoute.WorkOutScreen(ExerciseType.SQUAT, 0)
				) +
				DiagnosisRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				FeedRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				listOf(
					FeedRoute.PostDetailScreen(""),
					FeedRoute.CreatePostScreen()
				) +
				MyPageRoute::class.sealedSubclasses.mapNotNull { it.objectInstance }
	}
	
	private val routeMap: Map<String, Route> by lazy {
		allRoutes.associateBy { it::class.qualifiedName!!.replace('$', '.') }
	}

	fun findRouteByString(routeString: String?): Route? {
		if (routeString == null) return null
		val pureRoute = routeString.split('?', '/').first()
		
		return routeMap[pureRoute]
	}
	
	fun getNavItems(type: NavType) = navItemConfig[type] ?: emptyList()
	fun getScreenConfig(route: Route): ScreenConfig? {
		return screenConfig.entries.find { (key, _) -> key::class == route::class }?.value
	}
}