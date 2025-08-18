package com.cases.carefull.domain.util

sealed interface DataResourceResult<out T> {
    data class Success<T>(val data: T) : DataResourceResult<T>
    data class Error(val exception: Throwable) : DataResourceResult<Nothing> // Error?Failure
    data object Loading : DataResourceResult<Nothing>
}