package com.cases.carefull.data.repository.exercise

import android.annotation.SuppressLint
import com.cases.carefull.data.constant.FirestoreCollection
import com.cases.carefull.data.dto.exercise.ExerciseCollectionDto
import com.cases.carefull.data.dto.exercise.toDomainExerciseCollectionList
import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.repository.exercise.WorkOutRecordRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.cases.carefull.domain.util.WorkoutDateUtils
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WorkOutRecordRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
) : WorkOutRecordRepository {

    override fun fetchWorkOutStats(userId: String): Flow<DataResourceResult<List<ExerciseCollection>>> =
        callbackFlow {
            val listenerRegistration = db.collection(FirestoreCollection.WORK_OUT_COLLECTION)
                .whereEqualTo(FirestoreCollection.USER_ID, userId)
                .addSnapshotListener { snapshot, error ->
                    val result = runCatching {
                        if (error != null) throw error
                        val dtoList = snapshot?.toObjects<ExerciseCollectionDto>() ?: emptyList()
                        dtoList.toDomainExerciseCollectionList()
                    }
                    val response = result.fold(
                        onSuccess = { DataResourceResult.Success(it) },
                        onFailure = { DataResourceResult.Error(it) }
                    )
                    trySend(response)
                }
            awaitClose { listenerRegistration.remove() }
        }

    @SuppressLint("DefaultLocale")
    override suspend fun saveWorkOutCount(
        workOutStats: ExerciseCollection
    ): DataResourceResult<Boolean> = runCatching {
        if (workOutStats.count == 0) {
            return@runCatching DataResourceResult.Success(true)
        }
        val weekKey = WorkoutDateUtils.getWeeklyKey()
        val dailyKey = WorkoutDateUtils.getDailyKey()

        val countToAdd = workOutStats.count.toLong()
        val collectionRef = db.collection(FirestoreCollection.WORK_OUT_COLLECTION)
        val querySnapshot = collectionRef
            .whereEqualTo(FirestoreCollection.USER_ID, workOutStats.userId)
            .whereEqualTo(FirestoreCollection.CATEGORY_ID, workOutStats.exerciseType.name)
            .limit(1)
            .get()
            .await()
        if (querySnapshot.isEmpty) {
            val newDoc = ExerciseCollectionDto(
                user_id = workOutStats.userId,
                category_id = workOutStats.exerciseType.name,
                count = workOutStats.count,
                weekly_counts = mapOf(weekKey to workOutStats.count),
                daily_counts = mapOf(dailyKey to workOutStats.count)
            )
            collectionRef.add(newDoc).await()
        } else {
            val docRef = querySnapshot.documents.first().reference
            docRef.update(
                mapOf(
                    "count" to FieldValue.increment(countToAdd),
                    "weekly_counts.$weekKey" to FieldValue.increment(countToAdd),
                    "daily_counts.$dailyKey" to FieldValue.increment(countToAdd),
                    "updated_at" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        DataResourceResult.Success(true)
    }.getOrElse { exception ->
        DataResourceResult.Error(exception)
    }
}
