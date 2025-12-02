package com.cases.carefull.data.repository.routine.exercise

import android.content.SharedPreferences
import androidx.core.content.edit
import com.cases.carefull.data.constant.FirestoreCollection
import com.cases.carefull.data.dto.routine.ExerciseCollectionDto
import com.cases.carefull.domain.model.routine.exercise.ExerciseType
import com.cases.carefull.domain.repository.routine.exercise.TodayWorkOutRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

class TodayWorkOutRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val prefs: SharedPreferences
) : TodayWorkOutRepository {

    private companion object {
        const val KEY_LAST_FETCH_DATE = "last_fetch_date"
        const val KEY_DAILY_EXERCISE = "today_work_out_name"
    }

    override fun fetchTodayWorkOut(): Flow<DataResourceResult<ExerciseType>> = flow {
        emit(DataResourceResult.Loading)
        val result = runCatching {
            val todayString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val lastFetchDate = prefs.getString(KEY_LAST_FETCH_DATE, null)

            if (todayString == lastFetchDate) {
                val savedName = prefs.getString(KEY_DAILY_EXERCISE, null)
                val savedExercise = ExerciseType.entries.find { it.name == savedName }
                if (savedExercise != null) {
                    return@runCatching savedExercise
                }
            }
            val newDailyExercise = ExerciseType.entries.random()
            prefs.edit {
                putString(KEY_LAST_FETCH_DATE, todayString)
                putString(KEY_DAILY_EXERCISE, newDailyExercise.name)
            }
            newDailyExercise
        }
        emit(
            result.fold(
                onSuccess = { DataResourceResult.Success(it) },
                onFailure = { DataResourceResult.Error(it) }
            )
        )
    }

    override fun getTodayWorkOutCount(
        userId: String,
        exerciseType: ExerciseType
    ): Flow<DataResourceResult<Int>> =
        callbackFlow {
            val zoneId = ZoneId.systemDefault()
            val todayStart = LocalDate.now().atStartOfDay(zoneId).toInstant()
            val tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay(zoneId).toInstant()

            val listenerRegistration = db.collection(FirestoreCollection.WORK_OUT_COLLECTION)
                .whereEqualTo(FirestoreCollection.USER_ID, userId)
                .whereEqualTo(FirestoreCollection.CATEGORY_ID, exerciseType.name)
                .whereGreaterThanOrEqualTo(
                    FirestoreCollection.UPDATED_AT,
                    Date.from(todayStart)
                )
                .whereLessThan(FirestoreCollection.UPDATED_AT, Date.from(tomorrowStart))
                .addSnapshotListener { snapshot, error ->
                    val result = runCatching {
                        if (error != null) throw error

                        val totalCount = snapshot?.toObjects(ExerciseCollectionDto::class.java)
                            ?.sumOf { it.count } ?: 0

                        totalCount
                    }
                    val response = result.fold(
                        onSuccess = { DataResourceResult.Success(it) },
                        onFailure = { DataResourceResult.Error(it) }
                    )
                    trySend(response)
                }

            awaitClose { listenerRegistration.remove() }
        }
}
