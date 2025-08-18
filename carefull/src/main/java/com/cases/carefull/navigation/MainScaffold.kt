package com.cases.carefull.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.cases.carefull.common.MainViewModel
import com.cases.carefull.features.carefullcommon.components.BottomNavigationBar
import com.cases.carefull.features.carefullcommon.components.SubTopNavigationBar
import com.cases.carefull.features.carefullcommon.components.TopNavigationBar

@Composable
fun MainScaffold(
	viewModel: MainViewModel,
	navController: NavController,
	content: @Composable (PaddingValues) -> Unit
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	
	Scaffold(
		topBar = {
			if (uiState.topNavItems.isNotEmpty()) {
				TopNavigationBar(
					items = uiState.topNavItems,
					onTabSelected = { navController.navigate(it) }
				)
			}
		},
		bottomBar = {
			if (uiState.showBottomBar) {
				BottomNavigationBar(
					items = uiState.bottomNavItems,
					onTabSelected = { route ->
						navController.navigate(route) {
							popUpTo(navController.graph.findStartDestination().id) {
								saveState = true
							}
							launchSingleTop = true
							restoreState = true
						}
					}
				)
			}
		}
	) { innerPadding ->
		Column(
			Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			if (uiState.subTopNavItems.isNotEmpty()) {
				SubTopNavigationBar(
					items = uiState.subTopNavItems,
					onTabSelected = { route -> navController.navigate(route) }
				)
			}
			Box(modifier = Modifier.weight(1f)) {
				content(PaddingValues())
			}
		}
	}
}