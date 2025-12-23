package com.cases.carefull.domain.model.routine.diet

data class MyPagingData<T : Any>(
    val items: List<T>,
    val totalCount: Int,
    val isLastPage: Boolean
)
