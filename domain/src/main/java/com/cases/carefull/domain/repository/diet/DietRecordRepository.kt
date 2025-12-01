package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.FoodItem
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface DietRecordRepository {
    fun getAllMeal(): Flow<DataResourceResult<Map<LocalDate, List<FoodItem>>>>
    fun getMealByDate(date: LocalDate, userId: String): Flow<DataResourceResult<List<FoodItem>>>
    fun getMealsByMonth(
        yearMonth: YearMonth,
        userId: String
    ): Flow<DataResourceResult<Map<LocalDate, List<FoodItem>>>>

    suspend fun addMeal(
        foodItem: FoodItem,
        userId: String,
        mealType: String,
        date: LocalDate
    ): DataResourceResult<Unit>

    suspend fun removeMeal(documentId: String): DataResourceResult<Unit>
}
