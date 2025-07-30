package com.cases.carefull.features.carefullcontents.routine


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.data.network.FoodData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FoodSearchUiState(
	val foodList: List<FoodData> = emptyList(),
	val errorMessage: String? = null,
	val isLoading: Boolean = false
)


class FoodSearchViewModel(private val apiService: DietApiService) : ViewModel() {
	private val _uiState = MutableStateFlow(FoodSearchUiState())
	val uiState = _uiState.asStateFlow()
	
	fun searchFood(apiKey: String, foodName: String) {
		if (_uiState.value.isLoading) return
		
		viewModelScope.launch {
			_uiState.value = FoodSearchUiState(isLoading = true)
			try {
				val response = apiService.getFoodList(
					apiKey = apiKey,
					foodName = foodName
				)
				
				if (response.isSuccessful) {
					val apiResponse = response.body()
					val header = apiResponse?.header
					val body = apiResponse?.body
					
					if (header?.resultCode == "00") {
						val items = body?.items ?: emptyList()
						// 성공 시, foodList를 포함한 새로운 UiState로 업데이트
						_uiState.value = FoodSearchUiState(foodList = items)
						if (items.isEmpty()) {
							// 결과가 없는 경우, errorMessage를 포함한 새로운 UiState로 업데이트
							_uiState.value = FoodSearchUiState(errorMessage = "검색 결과가 없습니다.")
						}
						Log.d("API_SUCCESS", "데이터 로드 성공: ${items.size}개")
					} else {
						val errorMsg = header?.resultMsg ?: "알 수 없는 API 오류입니다."
						_uiState.value = FoodSearchUiState(errorMessage = errorMsg)
						Log.e("API_FAIL", errorMsg)
					}
				} else {
					val errorMsg = "API 호출 실패: ${response.code()}"
					_uiState.value = FoodSearchUiState(errorMessage = errorMsg)
					Log.e("API_ERROR", errorMsg)
				}
			} catch (e: Exception) {
				val errorMsg = "예외 발생: ${e.message}"
				_uiState.value = FoodSearchUiState(errorMessage = errorMsg)
				Log.e("API_EXCEPTION", "Exception", e)
			}
		}
	}
}

class FoodSearchViewModelFactory(
	private val apiService: DietApiService
) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(FoodSearchViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return FoodSearchViewModel(apiService) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}