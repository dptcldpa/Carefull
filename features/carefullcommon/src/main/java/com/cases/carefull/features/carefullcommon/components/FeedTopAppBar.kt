package com.cases.carefull.features.carefullcommon.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CustomTopAppBar(
	title: String,
	modifier: Modifier = Modifier,
	navigationIcon: @Composable (() -> Unit)? = null,
	actions: @Composable (RowScope.() -> Unit)? = null
) {
	Surface(
		modifier = modifier
			.fillMaxWidth()
			.height(56.dp),
//		shadowElevation = 4.dp
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 4.dp)
		) {
			if (navigationIcon != null) {
				Box(modifier = Modifier.align(Alignment.CenterStart)) {
					navigationIcon()
				}
			}
			
			Text(
				text = title,
				style = MaterialTheme.typography.titleLarge,
				textAlign = TextAlign.Center,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				modifier = Modifier
					.align(Alignment.Center)
					.padding(horizontal = 48.dp)
			)
			if (actions != null) {
				Row(
					modifier = Modifier.align(Alignment.CenterEnd),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.End,
					content = actions
				)
			}
		}
	}
}