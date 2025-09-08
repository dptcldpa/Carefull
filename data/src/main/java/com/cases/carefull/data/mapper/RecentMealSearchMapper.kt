package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.RecentMealSearchEntity
import com.cases.carefull.domain.model.diet.RecentMealSearch

fun RecentMealSearchEntity.toDomain(): RecentMealSearch {
	return RecentMealSearch(
		id = this.id,
		name = this.name,
		time = this.timestamp
	)
}

fun RecentMealSearch.toEntity(): RecentMealSearchEntity {
	return RecentMealSearchEntity(
		id = this.id,
		name = this.name,
		timestamp = this.time
	)
}