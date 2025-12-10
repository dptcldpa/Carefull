package com.cases.carefull.domain.model.feed

sealed class FeedException : Exception() {
    data object NotFoundPost : FeedException()
    data object NotFoundRank : FeedException()
    data object Unauthorized : FeedException()
    data object NetworkError : FeedException()
    data object Unknown : FeedException()
}
