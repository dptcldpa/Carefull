package com.cases.carefull.data.repository.diet

import com.cases.carefull.data.dto.diet.FoodCollectionDto
import com.cases.carefull.data.dto.diet.toDomainFoodCollection
import com.cases.carefull.data.dto.diet.toFirestoreFoodCollectionDto
import com.cases.carefull.domain.model.diet.FoodItem
import com.cases.carefull.data.constant.FirestoreCollection
import com.cases.carefull.domain.repository.diet.DietRecordRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

class DietRecordRepositoryImpl @Inject constructor(
) : DietRecordRepository {
    private val db = Firebase.firestore

    override fun getAllMeal(): Flow<DataResourceResult<Map<LocalDate, List<FoodItem>>>> {
        val query = db.collection(FirestoreCollection.FOOD_COLLECTION)
            .orderBy(FirestoreCollection.CREATED_AT, Query.Direction.DESCENDING)

        return query.snapshots()
            .map { snapshot ->
                val mealList = snapshot.documents.mapNotNull { document ->
                    val dto = document.toObject<FoodCollectionDto>()
                    dto?.toDomainFoodCollection()?.copy(
                        documentId = document.id
                    )
                }
                val mealsGroupedByDate = mealList.groupBy { dietCollection ->
                    Instant.ofEpochMilli(dietCollection.createdAt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }
                DataResourceResult.Success(mealsGroupedByDate) as DataResourceResult<Map<LocalDate, List<FoodItem>>>
            }
            .catch { exception ->
                emit(DataResourceResult.Error(exception))
            }.flowOn(Dispatchers.IO)
    }


    override fun getMealByDate(
        date: LocalDate,
        userId: String
    ): Flow<DataResourceResult<List<FoodItem>>> {
        val startOfDay = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endOfDay = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val query = db.collection(FirestoreCollection.FOOD_COLLECTION)
            .whereEqualTo(FirestoreCollection.USER_ID, userId)
            .whereGreaterThanOrEqualTo(FirestoreCollection.CREATED_AT, startOfDay)
            .whereLessThan(FirestoreCollection.CREATED_AT, endOfDay)
            .orderBy(FirestoreCollection.CREATED_AT, Query.Direction.DESCENDING)

        return query.snapshots()
            .map { snapshot ->
                val mealList = snapshot.documents.mapNotNull { document ->
                    val dto = document.toObject<FoodCollectionDto>()
                    dto?.toDomainFoodCollection()?.copy(documentId = document.id)
                }
                DataResourceResult.Success(mealList) as DataResourceResult<List<FoodItem>>
            }
            .catch { exception ->
                emit(DataResourceResult.Error(exception))
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getMealsByMonth(
        yearMonth: YearMonth,
        userId: String
    ): Flow<DataResourceResult<Map<LocalDate, List<FoodItem>>>> {
        val startOfMonth = Date.from(
            yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val endOfMonth = Date.from(
            yearMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val query = db.collection(FirestoreCollection.FOOD_COLLECTION)
            .whereEqualTo(FirestoreCollection.USER_ID, userId)
            .whereGreaterThanOrEqualTo(FirestoreCollection.CREATED_AT, startOfMonth)
            .whereLessThan(FirestoreCollection.CREATED_AT, endOfMonth)
            .orderBy(FirestoreCollection.CREATED_AT, Query.Direction.DESCENDING)

        return query.snapshots()
            .map { snapshot ->
                val mealList = snapshot.documents.mapNotNull { document ->
                    val dto = document.toObject<FoodCollectionDto>()
                    dto?.toDomainFoodCollection()?.copy(documentId = document.id)
                }
                val groupedMap = mealList.groupBy { foodItem ->
                    Instant.ofEpochMilli(foodItem.createdAt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }
                DataResourceResult.Success(groupedMap) as DataResourceResult<Map<LocalDate, List<FoodItem>>>
            }
            .catch { exception ->
                emit(DataResourceResult.Error(exception))
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun addMeal(
        foodItem: FoodItem,
        userId: String,
        mealType: String,
        date: LocalDate
    ): DataResourceResult<Unit> = runCatching {
        val recordToSave = foodItem.copy(
            userId = userId,
            type = mealType,
            createdAt = date.atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            updatedAt = System.currentTimeMillis()
        )
        val dto = recordToSave.toFirestoreFoodCollectionDto()

        db.collection(FirestoreCollection.FOOD_COLLECTION)
            .add(dto)
            .await()
        Unit
    }.map {
        DataResourceResult.Success(it)
    }.getOrElse { exception ->
        DataResourceResult.Error(exception)
    }

    override suspend fun removeMeal(documentId: String): DataResourceResult<Unit> = runCatching {
        db.collection(FirestoreCollection.FOOD_COLLECTION)
            .document(documentId)
            .delete()
            .await()
        Unit
    }.map {
        DataResourceResult.Success(it)
    }.getOrElse { exception ->
        DataResourceResult.Error(exception)
    }
}
