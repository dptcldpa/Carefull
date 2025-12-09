package com.cases.carefull.data.datasource

import com.cases.carefull.data.constant.FirestoreCollection
import com.cases.carefull.data.dto.routine.ExerciseCollectionDto
import com.cases.carefull.domain.util.FeedConfig
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RankingDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RankingDataSource {

    override suspend fun getTopRankings(sportName: String): List<ExerciseCollectionDto> {
        return firestore.collection(FirestoreCollection.WORK_OUT_COLLECTION)
            .whereEqualTo(FirestoreCollection.CATEGORY_ID, sportName)
            .orderBy(FirestoreCollection.COUNT, Query.Direction.DESCENDING)
            .limit(FeedConfig.RANK_SIZE)
            .get()
            .await()
            .toObjects(ExerciseCollectionDto::class.java)
    }

    override suspend fun getMyRecord(userId: String, sportName: String): ExerciseCollectionDto? {
        return firestore.collection(FirestoreCollection.WORK_OUT_COLLECTION)
            .whereEqualTo(FirestoreCollection.CATEGORY_ID, sportName)
            .whereEqualTo(FirestoreCollection.USER_ID, userId)
            .limit(1)
            .get()
            .await()
            .toObjects(ExerciseCollectionDto::class.java)
            .firstOrNull()
    }

    override suspend fun getCountGreaterThan(sportName: String, score: Int): Long {
        val snapshot = firestore.collection(FirestoreCollection.WORK_OUT_COLLECTION)
            .whereEqualTo(FirestoreCollection.CATEGORY_ID, sportName)
            .whereGreaterThan(FirestoreCollection.COUNT, score)
            .count()
            .get(AggregateSource.SERVER)
            .await()
        return snapshot.count
    }
}
