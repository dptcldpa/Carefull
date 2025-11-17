package com.cases.carefull.features.carefullcontents.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.cases.carefull.features.carefullcommon.navigation.FeedRoute
import com.cases.carefull.features.carefullcontents.feed.social.CreatePostScreen
import com.cases.carefull.features.carefullcontents.feed.social.PostDetailScreen
import com.cases.carefull.features.carefullcontents.feed.ranking.RankingScreen
import com.cases.carefull.features.carefullcontents.feed.social.SocialListScreen

fun NavGraphBuilder.feedGraph(navController: NavHostController) {
	composable<FeedRoute.SocialListScreen> {
		SocialListScreen(navController = navController)
	}
	composable<FeedRoute.PostDetailScreen> {
		PostDetailScreen(navController = navController)
	}
	composable<FeedRoute.CreatePostScreen> {
		CreatePostScreen(navController = navController)
	}
	composable<FeedRoute.RankingScreen> {
		RankingScreen()
	}
}