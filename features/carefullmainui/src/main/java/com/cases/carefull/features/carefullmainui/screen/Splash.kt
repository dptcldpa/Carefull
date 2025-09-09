package com.cases.carefull.features.carefullmainui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.navigation.MainRoute
import com.cases.carefull.features.carefullmainui.screen.auth.OAuthViewModel
import kotlinx.coroutines.delay


@Composable
fun Splash(
	viewModel: OAuthViewModel = hiltViewModel(),
	navController: NavController,
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	
	LaunchedEffect(key1 = uiState.isLoading) {
		if (!uiState.isLoading) {
			delay(1500L)
			
			if (uiState.userInfo != null) {
				navController.navigate(MainRoute.HomeScreen) {
					popUpTo(MainRoute.Splash) { inclusive = true }
					launchSingleTop = true
				}
			} else if (uiState.errorMessage == null) {
				navController.navigate(MainRoute.SigninScreen) {
					popUpTo(MainRoute.Splash) { inclusive = true }
					launchSingleTop = true
				}
			}
		}
	}
	
	LaunchedEffect(Unit) {
		viewModel.checkLoggedInState()
	}
	Column(
		modifier = Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			modifier = Modifier
				.padding(top = 150.dp),
			text = stringResource(R.string.app_name),
			style = MaterialTheme.typography.titleLarge
		)
		
		Spacer(modifier = Modifier.height(50.dp))
		
		Image(
			painter = painterResource(R.drawable.app_logo),
			contentDescription = null,
			modifier = Modifier
				.padding(top = 50.dp)
				.sizeIn(200.dp, 200.dp)
				.clip(RoundedCornerShape(8.dp)),
			contentScale = ContentScale.Crop,
		)
	}
}
