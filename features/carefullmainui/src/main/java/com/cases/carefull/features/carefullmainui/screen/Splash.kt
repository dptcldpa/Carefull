package com.cases.carefull.features.carefullmainui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
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
	
	LaunchedEffect(uiState.isAuthenticating) {
		if (!uiState.isAuthenticating) {
			delay(1000L)
			val destination = if (uiState.userInfo != null) {
				MainRoute.HomeScreen
			} else {
				MainRoute.SigninScreen
			}
			
			navController.navigate(destination) {
				popUpTo(MainRoute.Splash) { inclusive = true }
				launchSingleTop = true
			}
		}
	}
	
	Column(
		modifier = Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
//		Text(
//			modifier = Modifier
//				.padding(top = 150.dp),
//			text = stringResource(R.string.app_name),
//			style = MaterialTheme.typography.titleLarge
//		)

//		Spacer(modifier = Modifier.height(50.dp))
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			
			
			Image(
				painter = painterResource(R.drawable.app_logo),
				contentDescription = null,
				modifier = Modifier
					.sizeIn(100.dp, 100.dp)
					.clip(RoundedCornerShape(8.dp)),
				contentScale = ContentScale.Crop,
			)
		}
	}
}
