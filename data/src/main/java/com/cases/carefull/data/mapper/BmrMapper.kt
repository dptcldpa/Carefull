package com.cases.carefull.data.mapper

import com.cases.carefull.data.model.BmrCollection
import com.cases.carefull.domain.model.diet.Bmr

fun BmrCollection.toDomainModel(): Bmr {
	return Bmr(
		userId = this.userId,
		gender = this.gender,
		age = this.age,
		height = this.height,
		weight = this.weight,
		activity = this.activity,
		bmr = this.bmr,
		activityBmr = this.activityBmr
	)
}

fun Bmr.toDataModel(): BmrCollection {
	return BmrCollection(
		userId = this.userId,
		gender = this.gender,
		age = this.age,
		height = this.height,
		weight = this.weight,
		activity = this.activity,
		bmr = this.bmr,
		activityBmr = this.activityBmr
	)
}