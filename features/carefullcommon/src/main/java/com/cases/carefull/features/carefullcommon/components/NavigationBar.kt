package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cases.carefull.features.carefullcommon.model.NavItemUiModel
import com.cases.carefull.features.carefullcommon.navigation.Route

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
		edgePadding = 0.dp
	) {
		items.forEach { item ->
			Tab(
				selected = item.isSelected,
				onClick = { onTabSelected(item.spec.route) },
				text = { Text(item.spec.title, style = MaterialTheme.typography.bodyMedium) },
				selectedContentColor = MaterialTheme.colorScheme.primary,
				unselectedContentColor = MaterialTheme.colorScheme.onSurface
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
	TabRow(selectedTabIndex = items.indexOfFirst { it.isSelected }.coerceAtLeast(0)) {
		items.forEach { item ->
			Tab(
				selected = item.isSelected,
				onClick = { onTabSelected(item.spec.route) },
				text = { Text(text = item.spec.title, style = MaterialTheme.typography.bodyMedium) },
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
					label = {
						Text(
							text = item.spec.title,
							style = MaterialTheme.typography.labelSmall
						)
					}
				)
			}
		}
	}
}