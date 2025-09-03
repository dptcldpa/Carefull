package com.cases.carefull.domain.model.diet

data class DietCollection(
	val documentId:String = "",
	val userId: String = "",
	val mealType: String = "",
	val mealName: String = "",
	val kcal: Int = 0,
	val weight: Int = 0,
	val carbohydrate: Int = 0,
	val protein: Int = 0,
	val fat: Int = 0,
	val createdAt: Long = 0,
	val updatedAt: Long = 0
){
	fun divideWeight(updateWeight:Int): DietCollection{
		
		val caloriesPerGram = kcal.toDouble().div(weight)
		val carbsPerGram = carbohydrate.toDouble().div(weight)
		val proteinsPerGram = protein.toDouble().div(weight)
		val fatsPerGram = fat.toDouble().div(weight)
		
		val newCalories = (caloriesPerGram.times(updateWeight)).toInt()
		val newCarbs = (carbsPerGram.times(updateWeight)).toInt()
		val newProteins = (proteinsPerGram.times(updateWeight)).toInt()
		val newFats = (fatsPerGram.times(updateWeight)).toInt()
		
		return this.copy(
			weight = updateWeight,
			kcal = newCalories,
			carbohydrate = newCarbs,
			protein = newProteins,
			fat = newFats
		)
	}
}
