package com.cases.carefull.features.carefullcontents.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.cases.carefull.features.carefullcommon.navigation.FeedRoute
import com.cases.carefull.features.carefullcontents.feed.RankingScreen
import com.cases.carefull.features.carefullcontents.feed.Social

fun NavGraphBuilder.feedGraph(navController: NavHostController) {
	composable<FeedRoute.Social> {
		Social()
	}
	composable<FeedRoute.RankingScreen> {
		RankingScreen()
	}
}