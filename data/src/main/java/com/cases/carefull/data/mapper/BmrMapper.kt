package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.BmrCollection
import com.cases.carefull.domain.model.diet.Bmr

fun BmrCollection.toDomainModel(): Bmr {
	return Bmr(
		userId = this.userId,
		gender = this.gender,
		age = this.age,
		height = this.height,
		weight = this.weight,
		movementLevel = this.movementLevel,
		bmr = this.bmr,
		movementLevelBmr = this.movementLevelBmr
	)
}

fun Bmr.toDataModel(): BmrCollection {
	return BmrCollection(
		userId = this.userId,
		gender = this.gender,
		age = this.age,
		height = this.height,
		weight = this.weight,
		movementLevel = this.movementLevel,
		bmr = this.bmr,
		movementLevelBmr = this.movementLevelBmr
	)
}