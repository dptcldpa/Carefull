package com.cases.carefull.domain.model.common

enum class NavType {
	TOP_ROUTINE, TOP_DIAGNOSIS, TOP_FEED,
	SUB_DIAGNOSIS, SUB_SEARCH,
	BOTTOM_MAIN, NONE
}

data class ScreenConfig(
	val topBarType: NavType = NavType.NONE,
	val subTopBarType: NavType = NavType.NONE,
	val showBottomBar: Boolean = false
)