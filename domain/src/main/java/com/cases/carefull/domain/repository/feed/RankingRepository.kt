package com.cases.carefull.domain.repository.feed

import com.cases.carefull.domain.model.feed.MyRankInfo
import com.cases.carefull.domain.model.feed.Ranker
import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.domain.util.DataResourceResult

interface RankingRepository {
	suspend fun getRankingList(sport: ExerciseType): DataResourceResult<List<Ranker>>
	suspend fun getMyRanking(userId:String,sport: ExerciseType): DataResourceResult<MyRankInfo>
}
