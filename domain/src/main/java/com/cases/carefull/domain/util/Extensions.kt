package com.cases.carefull.domain.util

fun String?.toSafeInt(): Int {
    if (this.isNullOrBlank()) return 0
    return this.toDoubleOrNull()?.toInt() ?: 0
}

fun <T> Result<T>.toDataResourceResult(): DataResourceResult<T> {
    return this.fold(
        onSuccess = { DataResourceResult.Success(it) },
        onFailure = { DataResourceResult.Error(it) }
    )
}
