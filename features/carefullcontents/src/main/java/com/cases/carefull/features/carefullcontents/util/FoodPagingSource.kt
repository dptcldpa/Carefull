package com.cases.carefull.features.carefullcontents.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cases.carefull.domain.model.routine.diet.FoodItem
import com.cases.carefull.domain.repository.routine.diet.FoodSearchRepository
import kotlinx.coroutines.flow.first

class FoodPagingSource (
    private val foodSearchRepository: FoodSearchRepository,
    private val query: String
) : PagingSource<Int, FoodItem>() {

    companion object {
        const val PAGE_SIZE = 20
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoodItem> {
        val currentPage = params.key ?: 1

        return foodSearchRepository.searchFoodsByPage(
            query = query,
            page = currentPage,
            pageSize = PAGE_SIZE
        ).first()
            .fold(
                onSuccess = { pagingData ->
                    LoadResult.Page(
                        data = pagingData.items,
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = if (pagingData.isLastPage) null else currentPage + 1
                    )
                },
                onFailure = { exception ->
                    LoadResult.Error(exception)
                }
            )
    }

    override fun getRefreshKey(state: PagingState<Int, FoodItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
