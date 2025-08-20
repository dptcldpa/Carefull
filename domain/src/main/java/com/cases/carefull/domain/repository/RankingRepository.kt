package com.cases.carefull.domain.repository

import com.cases.carefull.domain.model.MyRankInfo
import com.cases.carefull.domain.model.Ranker
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.util.DataResourceResult

interface RankingRepository {
	suspend fun getAllUsersNicknameMap(): Map<String, String>
	suspend fun getRankingList(sport: ExerciseType): DataResourceResult<List<Ranker>>
	suspend fun getMyRanking(userId:String,sport: ExerciseType): DataResourceResult<MyRankInfo>
}