package com.openstudy.carefull.navigation

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

//상단 네비게이션 바
@Composable
fun TopNavigationBar(
    navController: NavController,
    topAppBarType: TopBarType
) {
    if (topAppBarType is TopBarType.None) {
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val selectedTabIndex = topAppBarType.tabs.indexOfFirst { tab ->
        currentDestination?.hierarchy?.any { dest ->
            dest.route == tab.route::class.qualifiedName
        } == true
    }.coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.statusBarsPadding(),
        indicator = { tabPositions -> },
        divider = {},
        edgePadding = 0.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        topAppBarType.tabs.forEachIndexed {index, tab ->
            Tab(
                selected = (selectedTabIndex == index),
                onClick = {
                    navController.navigate(tab.route) {
                        launchSingleTop = true
                    }
                },
                text = { Text(tab.title, style = MaterialTheme.typography.titleMedium) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

//상단 보조 네비게이션 바
@Composable
fun SubTopNavigationBar(
    navController: NavController,
    currentRoute: String?,
    subTopBarType: SubTopBarType
) {
    if (subTopBarType is SubTopBarType.None) {
        return
    }

    val selectedTabIndex = subTopBarType.tabs.indexOfFirst { tab ->
        tab.route::class.qualifiedName == currentRoute
    }.coerceAtLeast(0)

    TabRow(selectedTabIndex = selectedTabIndex) {
        subTopBarType.tabs.forEach { tab ->
            Tab(
                selected = (currentRoute == tab.route::class.qualifiedName),
                onClick = {
                    navController.navigate(tab.route) {
                        launchSingleTop = true
                    }
                },
                text = { Text(tab.title, style = MaterialTheme.typography.bodyMedium) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

//feed내 카테고리 탭 미구현
@Composable
fun SubFeedTopNavigationBar(
    navController: NavController,
    currentRoute: String?,
    subTopBarType: SubTopBarType
) {
    if (subTopBarType is SubTopBarType.None) {
        return
    }

    val selectedTabIndex = subTopBarType.tabs.indexOfFirst { tab ->
        tab.route::class.qualifiedName == currentRoute
    }.coerceAtLeast(0)

    ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
        subTopBarType.tabs.forEach { tab ->
            Tab(
                selected = (currentRoute == tab.route::class.qualifiedName),
                onClick = {
                    navController.navigate(tab.route) {
                        launchSingleTop = true
                    }
                },
                text = { Text(tab.title, style = MaterialTheme.typography.bodyMedium) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

//하단 네비게이션 바
@Composable
fun BottomNavigationBar(navController: NavController) {

    val bottomNavigationList = BottomNavigationList.items
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavigationList.forEach { bottomItem ->
            val isSelected = when (bottomItem.route) {
                NavigationRoute.ChatBotScreen -> {
                    currentDestination?.hierarchy?.any { dest ->
                        dest.route == NavigationRoute.ChatBotScreen::class.qualifiedName ||
                                dest.route == NavigationRoute.HospitalInfo::class.qualifiedName ||
                                dest.route == NavigationRoute.MedicineInfoScreen::class.qualifiedName ||
                                dest.route == NavigationRoute.DiseaseSearchScreen::class.qualifiedName
                    } == true
                }
                NavigationRoute.Exercise -> {
                    currentDestination?.hierarchy?.any { dest ->
                        dest.route == NavigationRoute.Exercise::class.qualifiedName ||
                                dest.route == NavigationRoute.Diet::class.qualifiedName ||
                                dest.route == NavigationRoute.SearchFood::class.qualifiedName
                    } == true
                }
                NavigationRoute.Social -> {
                    currentDestination?.hierarchy?.any { dest ->
                        dest.route == NavigationRoute.Social::class.qualifiedName ||
                                dest.route == NavigationRoute.Ranking::class.qualifiedName
                    } == true
                }
                else -> {
                    currentDestination?.hierarchy?.any { it.route == bottomItem.route::class.qualifiedName } == true
                }
            }

            NavigationBarItem(
                selected = isSelected,
                label = {
                    Text(
                        text = bottomItem.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                icon = {
                    Icon(
                        bottomItem.icon,
                        contentDescription = bottomItem.title
                    )
                },
                onClick = {
                    navController.navigate(bottomItem.route) {
                        // 스택의 최상단에 동일한 화면이 있다면 새로 띄우지 않음
                        launchSingleTop = true
                        // 이전에 저장된 상태가 있다면 복원
                        //  restoreState = true
                    }
                }
            )
        }
    }
}