package com.cases.carefull.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.cases.carefull.data.dto.ExerciseCollectionDTO
import com.cases.carefull.data.dto.toDomainExerciseCollectionList
import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.repository.ExerciseRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Calendar

class ExerciseRepositoryImpl(
    private val context: Context
) : ExerciseRepository {
    private val db = Firebase.firestore
    private companion object {
        const val PREFS_NAME = "daily_exercise_prefs"
        const val KEY_LAST_FETCH_DATE = "last_fetch_date"
        const val KEY_DAILY_EXERCISE = "daily_exercise_name"
        const val COMPLETION_PREFS_NAME = "exercise_completion_prefs"
        const val KEY_COMPLETED_DATES_PREFIX = "completed_dates_"
    }
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    private val completionPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(COMPLETION_PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
	override fun getCompletedDailyExerciseDatesFlow(userId: String): Flow<Set<LocalDate>> = callbackFlow {
        val key = "$KEY_COMPLETED_DATES_PREFIX$userId"
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, changedKey ->
            if (changedKey == key) {
                val dateStrings = sharedPreferences.getStringSet(key, emptySet()) ?: emptySet()
                trySend(dateStrings.map { LocalDate.parse(it) }.toSet())
            }
        }
        val initialDateStrings = completionPrefs.getStringSet(key, emptySet()) ?: emptySet()
        trySend(initialDateStrings.map { LocalDate.parse(it) }.toSet())
        completionPrefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { completionPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    
    override fun getExerciseStatFlow(userId: String): Flow<List<ExerciseCollection>> = callbackFlow {
        val listenerRegistration = db.collection("work_out_collection")
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val dtoList = snapshot.toObjects<ExerciseCollectionDTO>()
                    trySend(dtoList.toDomainExerciseCollectionList())
                }
            }
        awaitClose { listenerRegistration.remove() }
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
    
    @SuppressLint("DefaultLocale")
	@RequiresApi(Build.VERSION_CODES.O)
	override suspend fun addExerciseRecord(
        exerciseRecord: ExerciseCollection
    ): Boolean = runCatching {
        val today = LocalDate.now()
        val weekFields = WeekFields.ISO
        val weekBasedYear = today.get(weekFields.weekBasedYear())
        val weekOfYear = today.get(weekFields.weekOfWeekBasedYear())
        val weekKey = "${weekBasedYear}-W${String.format("%02d", weekOfYear)}"
        val dailyKey = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        val countToAdd = exerciseRecord.count.toLong()
        
        val collectionRef = db.collection("work_out_collection")
        val query = collectionRef
            .whereEqualTo("user_id", exerciseRecord.userId)
            .whereEqualTo("category_id", exerciseRecord.exerciseType.name)
            .limit(1)
        
        val snapshot = query.get().await()
        
        if (snapshot.isEmpty) {
            val newDoc = ExerciseCollectionDTO(
                user_id = exerciseRecord.userId,
                category_id = exerciseRecord.exerciseType.name,
                count = exerciseRecord.count,
                weekly_counts = mapOf(weekKey to exerciseRecord.count),
                daily_counts = mapOf(dailyKey to exerciseRecord.count)
            )
            collectionRef.add(newDoc).await()
        } else {
            val docRef = snapshot.documents.first().reference
            docRef.update(
                mapOf(
                    "count" to FieldValue.increment(countToAdd),
                    "weekly_counts.$weekKey" to FieldValue.increment(countToAdd),
                    "daily_counts.$dailyKey" to FieldValue.increment(countToAdd),
                    "updated_at" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        true
    }.getOrElse {
        it.printStackTrace()
        false
    }
    
    override suspend fun getTodaysExerciseCount(userId: String, exerciseType: ExerciseType): Int {
        return runCatching {
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            val startOfToday = Timestamp(today.time)
            
            today.add(Calendar.DAY_OF_MONTH, 1)
            val startOfTomorrow = Timestamp(today.time)
            
            val snapshot = db.collection("work_out_collection")
                .whereEqualTo("user_id", userId)
                .whereEqualTo("category_id", exerciseType.name)
                .whereGreaterThanOrEqualTo("updated_at", startOfToday)
                .whereLessThan("updated_at", startOfTomorrow)
                .get()
                .await()
            
            val dtoList = snapshot.toObjects(ExerciseCollectionDTO::class.java)
            dtoList.sumOf { it.count }
        }.getOrElse {
            0
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
	override suspend fun getCompletedDailyExerciseDates(userId: String): Set<LocalDate> {
        val key = "$KEY_COMPLETED_DATES_PREFIX$userId"
        val dateStrings = completionPrefs.getStringSet(key, emptySet()) ?: emptySet()
        return dateStrings.map { LocalDate.parse(it) }.toSet()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
	override suspend fun markDailyExerciseAsCompleted(userId: String, date: LocalDate) {
        val key = "$KEY_COMPLETED_DATES_PREFIX$userId"
        val currentDates = getCompletedDailyExerciseDates(userId).map { it.toString() }.toMutableSet()
        currentDates.add(date.toString())
        completionPrefs.edit {
            putStringSet(key, currentDates)
        }
    }
}