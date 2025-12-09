package com.cases.carefull.domain.model.feed

sealed class FeedException : Exception() {
    data object NotFound : FeedException()
    data object Unauthorized : FeedException()
    data object NetworkError : FeedException()
    data class Unknown(override val message: String?) : FeedException()
}
