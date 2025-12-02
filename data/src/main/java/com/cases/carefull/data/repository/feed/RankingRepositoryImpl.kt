package com.cases.carefull.data.repository.feed

import com.cases.carefull.data.dto.routine.ExerciseCollectionDto
import com.cases.carefull.domain.model.feed.MyRankInfo
import com.cases.carefull.domain.model.feed.Ranker
import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.domain.repository.feed.RankingRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor() : RankingRepository {
	private val db = Firebase.firestore
	
	override suspend fun getAllUsersNicknameMap(): Map<String, String> {
		return try {
			val snapshot = db.collection("accounts").get().await()
			snapshot.documents.associate { doc ->
				doc.id to (doc.getString("nickname") ?: doc.id)
			}
		} catch (e: Exception) {
			emptyMap()
		}
	}
	
	override suspend fun getRankingList(sport: ExerciseType): DataResourceResult<List<Ranker>> =
		runCatching {
			val nicknameMap = getAllUsersNicknameMap()
			val rankingList =
				db.collection("work_out_collection")
					.whereEqualTo("category_id", sport.name)
					.orderBy("count", Query.Direction.DESCENDING)
					.limit(100.toLong())
					.get()
					.await()
			val dtoList = rankingList.toObjects(ExerciseCollectionDto::class.java)
			dtoList.map { dto ->
				Ranker(
					userId = dto.user_id,
					totalCount = dto.count,
					exerciseType = dto.category_id,
					nickname = nicknameMap[dto.user_id] ?: dto.user_id
				)
			}
		}.map { rankerList ->
			DataResourceResult.Success(rankerList)
			
		}.getOrElse { exception ->
			DataResourceResult.Error(exception)
		}
	
	override suspend fun getMyRanking(userId: String, sport: ExerciseType): DataResourceResult<MyRankInfo> =
		runCatching {
			val myRanking =
				db.collection("work_out_collection")
					.whereEqualTo("category_id", sport.name)
					.whereEqualTo("user_id", userId)
					.limit(1)
					.get()
					.await()
			val myRecordDto = myRanking.toObjects(ExerciseCollectionDto::class.java).firstOrNull()
			if (myRecordDto == null) {
				MyRankInfo(rank = -1, myRecord = null)
			} else {
				val myCount = myRecordDto.count
				val higherRankCountSnapshot = db.collection("work_out_collection")
					.whereEqualTo("category_id", sport.name)
					.whereGreaterThan("count", myCount)
					.count().get(AggregateSource.SERVER).await()
				val myRank = higherRankCountSnapshot.count.toInt() + 1
				val myRankerRecord = Ranker(
					userId = myRecordDto.user_id,
					totalCount = myRecordDto.count,
					exerciseType = myRecordDto.category_id,
					nickname = myRecordDto.user_id
				)
				MyRankInfo(rank = myRank, myRecord = myRankerRecord)
			}
		}.map { myRankInfo ->
			DataResourceResult.Success(myRankInfo)
		}.getOrElse { exception ->
			DataResourceResult.Error(exception)
		}
}