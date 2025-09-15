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
			NavItem("소셜", FeedRoute.SocialListScreen),
			NavItem("랭킹", FeedRoute.RankingScreen)
		),
		NavType.NONE to emptyList(),
		
		NavType.SUB_DIAGNOSIS to listOf(
			NavItem("챗봇", DiagnosisRoute.ChatBotScreen),
			NavItem("병원 정보", DiagnosisRoute.HospitalInfoScreen),
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
		RoutineRoute.DietScreen to ScreenConfig(
			topBarType = NavType.TOP_ROUTINE,
			subTopBarType = NavType.NONE,
			showBottomBar = true
		),
		RoutineRoute.DietSearchScreen to ScreenConfig(
			topBarType = NavType.TOP_ROUTINE,
			subTopBarType = NavType.NONE,
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
		RoutineRoute.BmrScreen to ScreenConfig(
			topBarType = NavType.NONE,
			showBottomBar = false
		),
		//진단
		DiagnosisRoute.ChatBotScreen to ScreenConfig(
			topBarType = NavType.TOP_DIAGNOSIS,
			subTopBarType = NavType.NONE,
			showBottomBar = true
		),
		DiagnosisRoute.HospitalInfoScreen to ScreenConfig(
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
			showBottomBar = false
		),
		FeedRoute.PostDetailScreen to ScreenConfig(
			showBottomBar = false
		),
		FeedRoute.RankingScreen to ScreenConfig(
			topBarType = NavType.TOP_FEED,
			showBottomBar = true
		),
		
		MyPageRoute.AccountManagement to ScreenConfig()
	)
	
	val allRoutes: List<Route> by lazy {
		MainRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				RoutineRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				DiagnosisRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				FeedRoute::class.sealedSubclasses.mapNotNull { it.objectInstance } +
				listOf(
					FeedRoute.PostDetailScreen(""),
					FeedRoute.CreatePostScreen()
				) +
				MyPageRoute::class.sealedSubclasses.mapNotNull { it.objectInstance }
	}
	
	private val routeMap: Map<String, Route> by lazy {
		allRoutes.associateBy { it::class.qualifiedName!! }
	}
	
	fun findRouteByString(routeString: String?): Route? {
		if (routeString == null) return null
		
		// '?' 이전의 문자열(순수 경로)만 사용하여 key로 사용
		val pureRoute = routeString.substringBefore("?")
		
		// Kotlin 내부 클래스는 $로 구분되므로, 라이브러리가 생성한 경로 문자열과 맞추기 위해 .으로 변경
		val formattedRoute = pureRoute.replace('$', '.')
		
		return routeMap[formattedRoute]
	}
	
	fun getNavItems(type: NavType) = navItemConfig[type] ?: emptyList()
	fun getScreenConfig(route: Route): ScreenConfig? {
		// screenConfig의 key와 route의 클래스 타입이 같은 첫 번째 항목을 찾습니다.
		return screenConfig.entries.find { (key, _) -> key::class == route::class }?.value
	}
}