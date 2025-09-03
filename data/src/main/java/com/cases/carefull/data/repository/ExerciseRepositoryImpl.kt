package com.cases.carefull.data.repository

import android.annotation.SuppressLint
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
import java.util.Locale

class ExerciseRepositoryImpl(
    private val context: Context
) : ExerciseRepository {
    private val db = Firebase.firestore
    private companion object {
        const val PREFS_NAME = "daily_exercise_prefs"
        const val KEY_LAST_FETCH_DATE = "last_fetch_date"
        const val KEY_DAILY_EXERCISE = "daily_exercise_name"
        
        // [추가] 오늘의 운동 완료 기록을 위한 SharedPreferences
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
        
        // 1. 리스너 정의
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, changedKey ->
            // 변경된 키가 우리가 감시하는 키와 일치할 때만 동작
            if (changedKey == key) {
                val dateStrings = sharedPreferences.getStringSet(key, emptySet()) ?: emptySet()
                // 변경된 최신 데이터를 Flow로 전달
                trySend(dateStrings.map { LocalDate.parse(it) }.toSet())
            }
        }
        
        // 2. 현재 값을 한 번 방출
        val initialDateStrings = completionPrefs.getStringSet(key, emptySet()) ?: emptySet()
        trySend(initialDateStrings.map { LocalDate.parse(it) }.toSet())
        
        // 3. 리스너 등록
        completionPrefs.registerOnSharedPreferenceChangeListener(listener)
        
        // 4. Flow가 취소되면 리스너 제거
        awaitClose { completionPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    
    override fun getExerciseStatFlow(userId: String): Flow<List<ExerciseCollection>> = callbackFlow {
        // 1. 실시간 변경을 감지할 리스너 등록
        val listenerRegistration = db.collection("work_out_collection")
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, error ->
                // 에러 발생 시 Flow를 에러 상태로 종료
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                // 스냅샷이 null이 아니면 데이터를 파싱하여 Flow로 전달
                if (snapshot != null) {
                    val dtoList = snapshot.toObjects<ExerciseCollectionDTO>()
                    trySend(dtoList.toDomainExerciseCollectionList())
                }
            }
        
        // 2. Flow의 수집이 중단되면 (Coroutine이 취소되면) 리스너를 자동으로 제거
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
        // 1. "년도-W주차" 형식의 키 생성 (예: "2025-W36")
        val today = LocalDate.now()
        val weekFields = WeekFields.ISO
        
        // [수정] 달력 연도 대신 '주차 기준 연도'를 가져옵니다.
        val weekBasedYear = today.get(weekFields.weekBasedYear())
        val weekOfYear = today.get(weekFields.weekOfWeekBasedYear())
        
        // 이제 키는 항상 같은 주에 대해 동일한 연도를 가집니다.
        // 예: 2024-12-30 -> "2025-W01", 2025-01-01 -> "2025-W01"
        val weekKey = "${weekBasedYear}-W${String.format("%02d", weekOfYear)}"
        val dailyKey = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        val countToAdd = exerciseRecord.count.toLong()
        
        val collectionRef = db.collection("work_out_collection")
        val query = collectionRef
            .whereEqualTo("user_id", exerciseRecord.userId)
            .whereEqualTo("category_id", exerciseRecord.exerciseType)
            .limit(1)
        
        val snapshot = query.get().await()
        
        if (snapshot.isEmpty) {
            // 2. 문서가 없으면 새로 생성
            val newDoc = ExerciseCollectionDTO(
                user_id = exerciseRecord.userId,
                category_id = exerciseRecord.exerciseType,
                count = exerciseRecord.count,
                weekly_counts = mapOf(weekKey to exerciseRecord.count),
                daily_counts = mapOf(dailyKey to exerciseRecord.count)
            )
            collectionRef.add(newDoc).await()
        } else {
            // 3. 문서가 있으면 트랜잭션으로 안전하게 업데이트
            val docRef = snapshot.documents.first().reference
            docRef.update(
                mapOf(
                    // 총 횟수 누적
                    "count" to FieldValue.increment(countToAdd),
                    // weekly_counts Map의 특정 주차 횟수 누적 (dot notation 사용)
                    "weekly_counts.$weekKey" to FieldValue.increment(countToAdd),
                    "daily_counts.$dailyKey" to FieldValue.increment(countToAdd),
                    // 최종 업데이트 시간 갱신
                    "updated_at" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        true
    }.getOrElse {
        it.printStackTrace() // 디버깅을 위해 에러 출력
        false
    }
    
    
//    override suspend fun addExerciseRecord(
//        exerciseRecord: ExerciseCollection
//    ) = runCatching {
//
//        val querySnapshot = db.collection("work_out_collection")
//            .whereEqualTo("user_id", exerciseRecord.userId)
//            .whereEqualTo("category_id", exerciseRecord.exerciseType)
//            .limit(1)
//            .get()
//            .await()
//
//        if (querySnapshot.isEmpty) {
//            val dto = exerciseRecord.toFirestoreExerciseCollectionDTO()
//            db.collection("work_out_collection").add(dto).await()
//        } else {
//            val documentRef = querySnapshot.documents.first().reference
//            val countToAdd = exerciseRecord.count.toLong()
//            documentRef.update(
//                mapOf(
//                    "count" to FieldValue.increment(countToAdd),
//                    "updated_at" to FieldValue.serverTimestamp()
//                )
//            ).await()
//        }
//        true
//    }.getOrElse {
//        false
//    }

    override suspend fun resetExercise(exercise: ExerciseCollection): DataResourceResult<Unit> {
        TODO("Not yet implemented")
    }
    
    override suspend fun getTodaysExerciseCount(userId: String, exerciseType: ExerciseType): Int {
        return runCatching {
            // 오늘의 시작과 끝 타임스탬프 계산
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