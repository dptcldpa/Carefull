package com.cases.carefull.data.repository.feed.ranking

import com.cases.carefull.data.datasource.RankingDataSource
import com.cases.carefull.domain.model.feed.MyRankInfo
import com.cases.carefull.domain.model.feed.Ranker
import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.domain.repository.feed.RankingRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.domain.util.toDataResourceResult
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor(
    private val rankingDataSource: RankingDataSource,
    private val auth: FirebaseAuth
) : RankingRepository {

    override suspend fun getRankingList(sport: ExerciseType): DataResourceResult<List<Ranker>> =
        runCatching {
            val rankingDto = rankingDataSource.getTopRankings(sport.name)
            rankingDto.map { dto ->
                Ranker(
                    userId = dto.user_id,
                    totalCount = dto.count,
                    exerciseType = dto.category_id,
                    nickname = dto.user_id
                )
            }
        }.toDataResourceResult()

    override suspend fun getMyRanking(
        userId: String,
        sport: ExerciseType
    ): DataResourceResult<MyRankInfo> =
        runCatching {
            val myRecordDto = rankingDataSource.getMyRecord(userId, sport.name)

            if (myRecordDto == null) {
                MyRankInfo(rank = -1, myRecord = null)
            } else {
                val higherRankCount = rankingDataSource.getCountGreaterThan(sport.name, myRecordDto.count)
                val myRank = (higherRankCount + 1).toInt()

                val myRankerRecord = Ranker(
                    userId = myRecordDto.user_id,
                    totalCount = myRecordDto.count,
                    exerciseType = myRecordDto.category_id,
                    nickname = myRecordDto.user_id
                )

                MyRankInfo(rank = myRank, myRecord = myRankerRecord)
            }
        }.toDataResourceResult()

}