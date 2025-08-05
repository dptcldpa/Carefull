package com.cases.carefull.domain.model

data class DietInfo(
	val id: Long = System.currentTimeMillis(), 	 // 고유 ID
	val name: String?,       					 // 음식 이름
	val calories: Int?,     					 // 칼로리
	val carbs: Int?,							 // 탄수화물
	val proteins: Int?,							 // 단백질
	val fats: Int?,								 // 지방
	val weight: Int?,							 // 중량
	val mealType: MealType 						 // 식사 시간
){
	fun recalculateFor(newWeight:Int): DietInfo{
		if(weight == null || weight == 0){
			return this
		}
		
		val caloriesPerGram = calories?.toDouble()?.div(weight)
		val carbsPerGram = carbs?.toDouble()?.div(weight)
		val proteinsPerGram = proteins?.toDouble()?.div(weight)
		val fatsPerGram = fats?.toDouble()?.div(weight)
		
		val newCalories = (caloriesPerGram?.times(newWeight))?.toInt()
		val newCarbs = (carbsPerGram?.times(newWeight))?.toInt()
		val newProteins = (proteinsPerGram?.times(newWeight))?.toInt()
		val newFats = (fatsPerGram?.times(newWeight))?.toInt()
		
		return this.copy(
			weight = newWeight,
			calories = newCalories,
			carbs = newCarbs,
			proteins = newProteins,
			fats = newFats
		)
	}
}