package com.cases.carefull.features.carefullcommon.components

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ComposableToast(
	toastEvent: SharedFlow<String>,
	context: Context = LocalContext.current
){
	LaunchedEffect(key1 = true){
		toastEvent.collectLatest{
			Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
		}
	}
}