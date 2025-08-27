package com.cases.carefull.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.cases.carefull.data.model.ExerciseCollectionDTO
import com.cases.carefull.data.model.toDomainExerciseCollectionList
import com.cases.carefull.data.model.toFirestoreExerciseCollectionDTO
import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExerciseRepositoryImpl(
    private val context: Context
) : ExerciseRepository {
    private val db = Firebase.firestore
    private companion object {
        const val PREFS_NAME = "daily_exercise_prefs"
        const val KEY_LAST_FETCH_DATE = "last_fetch_date"
        const val KEY_DAILY_EXERCISE = "daily_exercise_name"
    }
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun getExerciseStat(userId: String) =
        runCatching {
            val snapshot = db.collection("work_out_collection")
                .whereEqualTo("user_id", userId)
                .get()
                .await()
            val dtoList = snapshot.toObjects(ExerciseCollectionDTO::class.java)
            dtoList.toDomainExerciseCollectionList()
        }.getOrElse {
            emptyList()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getDailyExerciseList(): List<ExerciseType> {
        val todayString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val lastFetchDate = prefs.getString(KEY_LAST_FETCH_DATE, null)
        if (todayString == lastFetchDate) {
            prefs.getString(KEY_DAILY_EXERCISE, null)
                ?.let { savedName -> ExerciseType.entries.find { it.name == savedName } }
                ?.let { savedExercise -> return listOf(savedExercise) }
        }
        val newDailyExercise = ExerciseType.entries.random()
        prefs.edit {
            putString(KEY_LAST_FETCH_DATE, todayString)
            putString(KEY_DAILY_EXERCISE, newDailyExercise.name)
        }
        return listOf(newDailyExercise)
    }

    override suspend fun addExerciseRecord(
        exerciseRecord: ExerciseCollection
    ) = runCatching {
        val querySnapshot = db.collection("work_out_collection")
            .whereEqualTo("user_id", exerciseRecord.userId)
            .whereEqualTo("category_id", exerciseRecord.exerciseType)
            .limit(1)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            val dto = exerciseRecord.toFirestoreExerciseCollectionDTO()
            db.collection("work_out_collection").add(dto).await()
        } else {
            val documentRef = querySnapshot.documents.first().reference
            val countToAdd = exerciseRecord.count.toLong()
            documentRef.update(
                mapOf(
                    "count" to FieldValue.increment(countToAdd),
                    "updated_at" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        true
    }.getOrElse {
        false
    }

    override suspend fun resetExercise(exercise: ExerciseCollection): DataResourceResult<Unit> {
        TODO("Not yet implemented")
    }
}