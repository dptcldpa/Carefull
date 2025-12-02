package com.cases.carefull.data.mapper

import com.cases.carefull.data.entity.RecentFoodSearchEntity
import com.cases.carefull.domain.model.routine.diet.RecentFoodSearch

fun RecentFoodSearchEntity.toDomain(): RecentFoodSearch {
    return RecentFoodSearch(
        query = this.query,
        searchedAt = this.searchedAt
    )
}

fun RecentFoodSearch.toEntity(): RecentFoodSearchEntity {
    return RecentFoodSearchEntity(
        query = this.query,
        searchedAt = this.searchedAt
    )
}
