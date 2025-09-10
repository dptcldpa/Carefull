package com.cases.carefull.features.carefullmainui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.cases.carefull.features.carefullcommon.navigation.MainRoute
import com.cases.carefull.features.carefullmainui.home.HomeScreen
import com.cases.carefull.features.carefullmainui.screen.Splash
import com.cases.carefull.features.carefullmainui.screen.auth.SigninScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(navController: NavHostController) {
	composable<MainRoute.Splash> {
		Splash(
			navController = navController
		)
	}
	composable<MainRoute.SigninScreen> {
		SigninScreen(
			navController = navController
		)
	}
	
	composable<MainRoute.HomeScreen> {
		HomeScreen(
			navController = navController
		)
	}
}