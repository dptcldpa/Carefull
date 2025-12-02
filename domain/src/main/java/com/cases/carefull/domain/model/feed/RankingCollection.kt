package com.cases.carefull.domain.model.feed

data class Ranker(
	val userId: String,
	val totalCount: Int,
	val exerciseType: String,
	val nickname: String
)

data class MyRankInfo(
	val rank: Int,
	val myRecord: Ranker?
)