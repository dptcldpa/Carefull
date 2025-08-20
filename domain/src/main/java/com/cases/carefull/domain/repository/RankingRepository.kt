package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.MyRankInfo
import com.cases.carefull.domain.model.Ranker
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.util.DataResult

interface RankingRepository {
	suspend fun getAllUsersNicknameMap(): Map<String, String>
	suspend fun getRankingList(sport: ExerciseType): DataResult<List<Ranker>>
	suspend fun getMyRanking(userId:String,sport: ExerciseType): DataResult<MyRankInfo>
}