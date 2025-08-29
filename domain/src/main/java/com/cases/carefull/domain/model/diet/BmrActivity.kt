package com.cases.carefull.domain.model.diet

enum class BmrActivity(
	val multiplier: Double,
	val description: String
) {
	NONE(
		multiplier = 1.2,
		description = "좌식 생활: 거의 운동하지 않으며, 대부분의 시간을 앉아서 보냄"
	),
	LIGHT(
		multiplier = 1.375,
		description = "가벼운 활동: 가벼운 운동이나 스포츠를 주 1~3회 즐김"
	),
	MEDIUM(
		multiplier = 1.55,
		description = "중간 활동: 보통 수준의 운동이나 스포츠를 주 3~5회 즐김"
	),
	HEAVY(
		multiplier = 1.725,
		description = "강한 활동: 격렬한 운동이나 스포츠를 주 6~7회 즐김"
	),
	EXTREME(
		multiplier = 1.9,
		description = "매우 강한 활동: 매우 격렬한 운동, 스포츠와 육체노동을 병행"
	);
}
