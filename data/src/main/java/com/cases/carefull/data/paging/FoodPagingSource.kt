package com.cases.carefull.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.domain.model.routine.diet.FoodItem

class FoodPagingSource(
    private val dietApiService: DietApiService,
    private val dietApiKey: String,
    private val query: String
) : PagingSource<Int, FoodItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoodItem> {
        val currentPage = params.key ?: 1

        return try {
            val response = dietApiService.getFoodList(
                apiKey = dietApiKey,
                query = query,
                pageNo = currentPage
            )

            if (response.header.resultCode == "00") {
                val items = response.body.items
                val domainList = items?.map { it.toDomain() } ?: emptyList()

                LoadResult.Page(
                    data = domainList,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (items.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(Exception(response.header.resultMsg))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FoodItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
