package com.cases.carefull.features.carefullmainui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.cases.carefull.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
	private val homeRepository: HomeRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState = _uiState.asStateFlow()
	
	fun onDateSelected(date: LocalDate, dateDifference: String) {
		_uiState.update {
			it.copy(
				selectedDate = date,
				selectedDateInfo = dateDifference
			)
		}
	}
}