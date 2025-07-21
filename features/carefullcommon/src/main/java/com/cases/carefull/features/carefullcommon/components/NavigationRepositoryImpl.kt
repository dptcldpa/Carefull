package com.cases.carefull.features.carefullcommon.components

import com.cases.carefull.domain.model.NavType
import com.cases.carefull.domain.model.ScreenConfig
import com.cases.carefull.features.carefullcommon.model.NavItem
import com.cases.carefull.features.carefullcommon.model.NavigationRepository
import com.cases.carefull.features.carefullcommon.navigation.Route

class NavigationRepositoryImpl : NavigationRepository {
	override fun getScreenConfig(route: Route): ScreenConfig? =LayoutAsset.getScreenConfig(route)
	override fun getNavItems(type: NavType): List<NavItem> =LayoutAsset.getNavItems(type)
}