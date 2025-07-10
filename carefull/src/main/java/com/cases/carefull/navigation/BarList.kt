package com.openstudy.carefull.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class TopBar(
    val title: String,
    val route: NavigationRoute
)

sealed class TopBarType(val tabs: List<TopBar>) {
    data object Routine : TopBarType(
        tabs = listOf(
            TopBar(title = "운동", route = NavigationRoute.Exercise),
            TopBar(title = "식단", route = NavigationRoute.Diet)
        )
    )

    data object Diagnosis : TopBarType(
        tabs = listOf(
            TopBar(title = "진료", route = NavigationRoute.ChatBotScreen),
            TopBar(title = "검색", route = NavigationRoute.DiseaseSearchScreen)
        )
    )

    data object Feed : TopBarType(
        tabs = listOf(
            TopBar(title = "소셜", route = NavigationRoute.Social),
            TopBar(title = "랭킹", route = NavigationRoute.Ranking)
        )
    )

    data object None : TopBarType(tabs = emptyList())
}

sealed class SubTopBarType(val tabs: List<TopBar>) {
    data object SubDiagnosis : SubTopBarType(
        tabs = listOf(
            TopBar(title = "챗봇", route = NavigationRoute.ChatBotScreen),
            TopBar(title = "병원정보", route = NavigationRoute.HospitalInfo),
            TopBar(title = "약정보", route = NavigationRoute.MedicineInfoScreen)
        )
    )

    data object SubSearch : SubTopBarType(
        tabs = listOf(
            TopBar(title = "병원", route = NavigationRoute.DiseaseSearchScreen),
            TopBar(title = "질환", route = NavigationRoute.DiseaseSearchScreen),
            TopBar(title = "약", route = NavigationRoute.DiseaseSearchScreen)
        )
    )

    data object None : SubTopBarType(tabs = emptyList())
}

//미완성
sealed class SubFeedTopBarType(val tabs: List<TopBar>) {
    data object SubSocial : SubTopBarType(
        tabs = listOf(
            TopBar(title = "전체", route = NavigationRoute.ChatBotScreen),
            TopBar(title = "운동", route = NavigationRoute.HospitalInfo),
            TopBar(title = "식단", route = NavigationRoute.MedicineInfoScreen),
            TopBar(title = "병원", route = NavigationRoute.MedicineInfoScreen)
        )
    )

    data object SubRanking : SubTopBarType(
        tabs = listOf(
            TopBar(title = "스쿼트", route = NavigationRoute.ChatBotScreen),
            TopBar(title = "벤치프레스", route = NavigationRoute.HospitalInfo),
            TopBar(title = "데드리프트", route = NavigationRoute.MedicineInfoScreen),
            TopBar(title = "푸시업", route = NavigationRoute.MedicineInfoScreen),
            TopBar(title = "레그레이즈", route = NavigationRoute.MedicineInfoScreen),
            TopBar(title = "사이드레터럴레이즈", route = NavigationRoute.MedicineInfoScreen),
            TopBar(title = "플랭크", route = NavigationRoute.MedicineInfoScreen)
        )
    )

    data object None : SubFeedTopBarType(tabs = emptyList())
}


sealed class BottomNavigationList(
    val route: NavigationRoute,
    val icon: ImageVector,
    val title: String
) {
    data object Home :
        BottomNavigationList(NavigationRoute.Home, Icons.Default.Home, "홈")

    data object Routine :
        BottomNavigationList(NavigationRoute.Exercise, Icons.Default.AreaChart, "루틴")

    data object Diagnosis :
        BottomNavigationList(NavigationRoute.ChatBotScreen, Icons.Default.LocalHospital, "진단")

    data object Feed :
        BottomNavigationList(NavigationRoute.Social, Icons.Default.ChatBubbleOutline, "피드")

    data object MyPage :
        BottomNavigationList(NavigationRoute.MyPage, Icons.Default.Person, "마이페이지")

    companion object {
        val items: List<BottomNavigationList> = listOf(
            Home,
            Routine,
            Diagnosis,
            Feed,
            MyPage
        )
        val routes: List<NavigationRoute>
            get() = items.map { it.route }
    }
}