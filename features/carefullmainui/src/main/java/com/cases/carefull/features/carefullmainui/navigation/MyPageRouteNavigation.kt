package com.cases.carefull.features.carefullmainui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.cases.carefull.features.carefullcommon.navigation.MyPageRoute
import com.cases.carefull.features.carefullmainui.screen.mypage.AccountManagement
import com.cases.carefull.features.carefullmainui.screen.mypage.MyPage
import com.cases.carefull.features.carefullmainui.screen.mypage.PostWrittenManagement

fun NavGraphBuilder.myPageGraph(navController: NavHostController) {
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