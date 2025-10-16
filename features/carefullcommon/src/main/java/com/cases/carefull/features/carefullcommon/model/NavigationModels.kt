package com.cases.carefull.features.carefullcommon.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.cases.carefull.domain.model.NavType
import com.cases.carefull.domain.model.ScreenConfig
import com.cases.carefull.features.carefullcommon.navigation.Route


interface AppNavigationProvider {
	fun getScreenConfig(route: Route): ScreenConfig?
	fun getNavItems(type: NavType): List<NavItem>
}

data class NavItem(
	val title: String,
	val route: Route,
	val iconName: String? = null // 하단바 아이콘
)

data class NavItemUiModel(
	val spec: NavItem,
	val isSelected: Boolean,
	val icon: ImageVector? = null // 하단바 아이콘
)

@Stable
data class MainUiState(
	val topNavItems: List<NavItemUiModel> = emptyList(),
	val subTopNavItems: List<NavItemUiModel> = emptyList(),
	val bottomNavItems: List<NavItemUiModel> = emptyList(),
	val showBottomBar: Boolean = false
)