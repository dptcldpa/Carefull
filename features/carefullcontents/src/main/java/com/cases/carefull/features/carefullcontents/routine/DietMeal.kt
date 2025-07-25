package com.cases.carefull.features.carefullcontents.routine

enum class MealType(val time: String) {
	BREAKFAST("아침"),
	LUNCH("점심"),
	DINNER("저녁"),
	SNACK("간식")
}

data class MealRecord(
	val id: Long = System.currentTimeMillis(), // 고유 ID
	val name: String,       // 음식 이름
	val calories: Int,      // 칼로리
	val mealType: MealType  // 어느 시간에 먹었는지
)