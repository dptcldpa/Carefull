package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cases.carefull.features.carefullcommon.model.NavItem
import com.cases.carefull.features.carefullcommon.model.NavItemUiModel
import com.cases.carefull.features.carefullcommon.navigation.MainRoute
import com.cases.carefull.features.carefullcommon.navigation.Route
import com.cases.carefull.features.carefullcommon.theme.CarefullTheme

//상단 바
@Composable
fun TopNavigationBar(
    items: List<NavItemUiModel>,
    onTabSelected: (Route) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = items.indexOfFirst { it.isSelected }.coerceAtLeast(0),
        modifier = Modifier.statusBarsPadding(),
        indicator = {},
        divider = {},
        edgePadding = 0.dp,
        containerColor = Color.White
    ) {
        items.forEach { item ->
            Tab(
                selected = item.isSelected,
                onClick = { onTabSelected(item.spec.route) },
                text = { Text(item.spec.title, style = MaterialTheme.typography.titleMedium) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

//상단 보조 바
@Composable
fun SubTopNavigationBar(
    items: List<NavItemUiModel>,
    onTabSelected: (Route) -> Unit
) {
    TabRow(
        selectedTabIndex = items.indexOfFirst { it.isSelected }.coerceAtLeast(0),
        containerColor = Color.White
    ) {
        items.forEach { item ->
            Tab(
                selected = item.isSelected,
                onClick = { onTabSelected(item.spec.route) },
                text = { Text(text = item.spec.title, style = MaterialTheme.typography.bodyLarge) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

//하단 바
@Composable
fun BottomNavigationBar(
    items: List<NavItemUiModel>,
    onTabSelected: (Route) -> Unit
) {
    Column {
        HorizontalDivider(
            modifier = Modifier,
            thickness = 2.dp,
        )
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    selected = item.isSelected,
                    onClick = { onTabSelected(item.spec.route) },
                    icon = { Icon(item.icon!!, contentDescription = item.spec.title) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    ),
                    label = {
                        Text(
                            text = item.spec.title,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopNavigationBarPreview() {
    val fakeItems = listOf(
        NavItemUiModel(
            spec = NavItem("진단", MainRoute.HomeScreen),
            isSelected = true,
            icon = Icons.Default.Home
        ),
        NavItemUiModel(
            spec = NavItem("검색", MainRoute.HomeScreen),
            isSelected = true,
            icon = Icons.Default.Home
        )
    )
    CarefullTheme {
        TopNavigationBar(fakeItems, {})
    }
}

@Preview(showBackground = true)
@Composable
fun SubTopNavigationBarPreview() {
    val fakeItems = listOf(
        NavItemUiModel(
            spec = NavItem("챗봇", MainRoute.HomeScreen),
            isSelected = true,
            icon = Icons.Default.Home
        ),
        NavItemUiModel(
            spec = NavItem("추천", MainRoute.HomeScreen),
            isSelected = true,
            icon = Icons.Default.Home
        )
    )
    CarefullTheme {
        SubTopNavigationBar(fakeItems, {})
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val fakeItems = listOf(
        NavItemUiModel(
            spec = NavItem("홈", MainRoute.HomeScreen),
            isSelected = true,
            icon = Icons.Default.Home
        ),
        NavItemUiModel(
            spec = NavItem("루틴", MainRoute.HomeScreen),
            isSelected = true,
            icon = Icons.Default.AreaChart
        ),
        NavItemUiModel(
            spec = NavItem("진단", MainRoute.HomeScreen),
            isSelected = true,
            icon = Icons.Default.LocalHospital
        ),
        NavItemUiModel(
            spec = NavItem("피드", MainRoute.HomeScreen),
            isSelected = true,
            icon = Icons.Default.ChatBubbleOutline
        ),
        NavItemUiModel(
            spec = NavItem("마이", MainRoute.HomeScreen),
            isSelected = true,
            icon = Icons.Default.Person
        )
    )
    CarefullTheme {
        BottomNavigationBar(fakeItems,{})
    }
}