package com.cases.carefull.data.repository.routine.diet

import com.cases.carefull.data.di.DietApiKey
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.domain.model.routine.diet.FoodItem
import com.cases.carefull.domain.model.routine.diet.MyPagingData
import com.cases.carefull.domain.repository.routine.diet.FoodSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FoodSearchRepositoryImpl @Inject constructor(
    private val apiService: DietApiService,
    @param:DietApiKey private val dietApiKey: String,
) : FoodSearchRepository {
    override fun searchFoodsByPage(
        query: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<MyPagingData<FoodItem>>> = flow {
        val response = apiService.getFoodList(
            apiKey = dietApiKey,
            query = query,
            pageNo = page,
            numOfRows = pageSize
        )

        if (response.header.resultCode == "00") {
            val items = response.body.items?.map { it.toDomain() } ?: emptyList()
            val totalCount = response.body.totalCount
            val isLastPage = (page * pageSize) >= totalCount

            val pagingData = MyPagingData(
                items = items,
                totalCount = totalCount,
                isLastPage = isLastPage
            )
            emit(Result.success(pagingData))
        } else {
            emit(Result.failure(Exception(response.header.resultMsg)))
        }
    }.catch { e ->
        emit(Result.failure(e))
    }.flowOn(Dispatchers.IO)
}
