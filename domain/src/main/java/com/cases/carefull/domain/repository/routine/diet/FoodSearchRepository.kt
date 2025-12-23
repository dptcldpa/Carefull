package com.cases.carefull.domain.repository.routine.diet

import com.cases.carefull.domain.model.routine.diet.FoodItem
import com.cases.carefull.domain.model.routine.diet.MyPagingData
import kotlinx.coroutines.flow.Flow

interface FoodSearchRepository {
    fun searchFoodsByPage(query: String, page: Int, pageSize: Int): Flow<Result<MyPagingData<FoodItem>>>
}
