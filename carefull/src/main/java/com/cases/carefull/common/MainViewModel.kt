package com.cases.carefull.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.cases.carefull.domain.model.NavType
import com.cases.carefull.domain.model.ScreenConfig
import com.cases.carefull.features.carefullcommon.model.MainUiState
import com.cases.carefull.features.carefullcommon.model.NavItem
import com.cases.carefull.features.carefullcommon.model.NavItemUiModel
import com.cases.carefull.features.carefullcommon.model.NavigationRepository
import com.cases.carefull.features.carefullcommon.navigation.DiagnosisRoute
import com.cases.carefull.features.carefullcommon.navigation.FeedRoute
import com.cases.carefull.features.carefullcommon.navigation.MainRoute
import com.cases.carefull.features.carefullcommon.navigation.MyPageRoute
import com.cases.carefull.features.carefullcommon.navigation.Route
import com.cases.carefull.features.carefullcommon.navigation.RoutineRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class MainViewModel(private val repository: NavigationRepository) : ViewModel() {

	private val _uiState = MutableStateFlow(MainUiState())
	val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
	
	fun onRouteChanged(currentRoute: Route?) {
		if (currentRoute == null) return
		val screenConfig = repository.getScreenConfig(currentRoute) ?: ScreenConfig()
		
		_uiState.update {
			it.copy(
				topNavItems = createUiModels(
					repository.getNavItems(screenConfig.topBarType),
					currentRoute
				),
				subTopNavItems = createUiModels(
					repository.getNavItems(screenConfig.subTopBarType),
					currentRoute
				),
				bottomNavItems = createUiModels(
					repository.getNavItems(NavType.BOTTOM_MAIN),
					currentRoute
				),
				showBottomBar = screenConfig.showBottomBar
			)
		}
	}
	
	private fun isRouteSelected(spec: NavItem, currentRoute: Route?): Boolean {
		if (currentRoute == null) return false
		val specRoute = spec.route
		if (spec.iconName != null) {
			return when (specRoute) {
				is MainRoute.Home -> currentRoute is MainRoute
				is RoutineRoute -> currentRoute is RoutineRoute
				is DiagnosisRoute -> currentRoute is DiagnosisRoute
				is FeedRoute -> currentRoute is FeedRoute
				is MyPageRoute -> currentRoute is MyPageRoute
				else -> false
			}
		}
		return specRoute == currentRoute
	}
	
	
	private fun createUiModels(
		specs: List<NavItem>,
		currentRoute: Route?
	): List<NavItemUiModel> {
		return specs.map { spec ->
			NavItemUiModel(
				spec = spec,
				isSelected = isRouteSelected(spec, currentRoute),
				icon = spec.iconName?.let { mapIconNameToVector(it) }
			)
		}
	}
	
	private fun mapIconNameToVector(name: String): ImageVector {
		return when (name.lowercase()) {
			"home" -> Icons.Default.Home
			"routine" -> Icons.Default.AreaChart
			"diagnosis" -> Icons.Default.LocalHospital
			"feed" -> Icons.Default.ChatBubbleOutline
			"mypage" -> Icons.Default.Person
			else -> Icons.Default.Error
		}
	}
}