package com.cases.carefull.features.carefullcontents.routine.diet

import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.MealType
import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.BmrActivity
import com.cases.carefull.domain.model.diet.Gender

data class DietUiState(
	val mealsByTime: Map<MealType, List<DietCollection>> = emptyMap(),
	val totalCalories: Int = 0,
	val totalCarbs: Int = 0,
	val totalProteins: Int = 0,
	val totalFats: Int = 0,
	
	val searchResults: List<DietCollection> = emptyList(),
	val isLoading: Boolean = true,
	val isError: Boolean = false,
	
	val mealTypeSelection: MealType? = null,
	
	val bmrState: BmrUiState = BmrUiState(),
	val savedBmr: Bmr? = null
) {
	// 2. 현재 입력값과 저장된 값을 비교하여 변경 여부를 알려주는 계산 프로퍼티 추가
	val isBmrChanged: Boolean
		get() {
			// 저장된 데이터가 없으면 무조건 '변경됨'으로 간주
			if (savedBmr == null) return true
			
			// 각 필드 비교
			val isGenderSame = (savedBmr.gender == (bmrState.gender == Gender.MALE))
			val isHeightSame = (savedBmr.height.toString() == bmrState.height)
			val isWeightSame = (savedBmr.weight.toString() == bmrState.weight)
			val isAgeSame = (savedBmr.age.toString() == bmrState.age)
			val isActivitySame = (savedBmr.activity == bmrState.activity)
			
			// 모든 필드가 동일-> 변경되지 않음 (false)
			// 하나라도 다르면 -> 변경됨 (true)
			return !(isGenderSame && isHeightSame && isWeightSame && isAgeSame && isActivitySame)
		}
}

data class BmrUiState(
	val gender: Gender = Gender.MALE,
	val height: String = "",
	val weight: String = "",
	val age: String = "",
	val activity: BmrActivity = BmrActivity.NONE,
	val calculatedBmr: Int = 0,
	val activityMetabolism: Int = 0,
	val isLoading: Boolean = false
)