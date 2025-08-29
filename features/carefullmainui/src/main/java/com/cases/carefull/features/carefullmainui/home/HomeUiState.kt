package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class HomeUiState(
	val selectedDate: LocalDate = LocalDate.now(),
	val selectedDateInfo: String = "오늘"
)
