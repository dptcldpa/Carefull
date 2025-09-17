package com.cases.carefull.features.carefullmainui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.navigation.MainRoute


@Composable
fun SigninScreen(
	viewModel: OAuthViewModel = hiltViewModel(),
	navController: NavController,
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val context = LocalContext.current
	
	LaunchedEffect(key1 = uiState.errorMessage) {
		uiState.errorMessage?.let {
			Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
			viewModel.errorMessageShown()
		}
	}
	
	Column(
		modifier = Modifier
			.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
//		Text(
//			modifier = Modifier
//				.padding(top = 50.dp),
//			text = "CareFull",
//			style = MaterialTheme.typography.titleLarge
//		)
		Spacer(modifier = Modifier.height(50.dp))
		Image(
			modifier = Modifier
				.padding(top = 50.dp)
				.size(200.dp)
				.clip(RoundedCornerShape(8.dp)),
			painter = painterResource(R.drawable.app_logo),
			contentDescription = null,
			contentScale = ContentScale.Crop,
		)
		
		Spacer(modifier = Modifier.height(90.dp))
		
		//카카오
		if (uiState.isLoading) {
			CircularProgressIndicator()
		} else {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Button(
					onClick = { viewModel.loginWithKakao() },
					modifier = Modifier
						.fillMaxWidth(0.8f)
						.height(56.dp),
					shape = RoundedCornerShape(12.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFFFFE000),
						contentColor = Color.Black
					),
					contentPadding = PaddingValues(0.dp)
				) {
					Image(
						painter = painterResource(id = R.drawable.kakao_login_large_wide),
						contentDescription = "카카오 로그인",
						modifier = Modifier.fillMaxSize()
					)
				}
//				Spacer(modifier = Modifier.height(50.dp))
				
				// 로그인
				Button(
					onClick = {
						navController.navigate(MainRoute.HomeScreen) {
							popUpTo(MainRoute.SigninScreen) {
								inclusive = true
							}
						}
					}, modifier = Modifier, colors = ButtonDefaults.buttonColors(
						containerColor = Color.Transparent,
						contentColor = Color.Black
					)
				) {
					Text(
						text = "다른 방법으로 시작하기",
						style = MaterialTheme.typography.bodySmall,
						color = Color.Gray,
						textDecoration = TextDecoration.Underline
					)
				}
				
			}
		}
		// 일단 홈 화면으로 이동하는 버튼 구현
//		Button(
//			onClick = {
//				navController.navigate(MainRoute.HomeScreen) {
//					popUpTo(MainRoute.SigninScreen) {
//						inclusive = true
//					}
//				}
//			}, modifier = Modifier, colors = ButtonDefaults.buttonColors(
//				containerColor = Color.Transparent,
//				contentColor = Color.Black
//			)
//		) {
//			Text(
//				text = "홈 화면으로 이동",
//				style = MaterialTheme.typography.bodySmall,
//				color = Color.Gray,
//				textDecoration = TextDecoration.Underline
//			)
//		}
	}
}