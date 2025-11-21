package com.cases.carefull.domain.repository.diet

import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.util.DataResourceResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DietRecordRepository {
    fun getAllMeal(): Flow<DataResourceResult<Map<LocalDate, List<DietCollection>>>>
    suspend fun addMeal(mealData: DietCollection): DataResourceResult<Unit>
    suspend fun removeMeal(documentId: String): DataResourceResult<Unit>
}