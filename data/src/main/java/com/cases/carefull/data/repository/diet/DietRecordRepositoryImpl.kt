package com.cases.carefull.data.repository.diet

import com.cases.carefull.data.dto.DietCollectionDTO
import com.cases.carefull.data.dto.toDomainDietCollection
import com.cases.carefull.data.dto.toFirestoreDietCollectionDTO
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.repository.diet.DietRecordRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class DietRecordRepositoryImpl @Inject constructor(
) : DietRecordRepository {
    private val db = Firebase.firestore

    override fun getAllMeal(): Flow<DataResourceResult<Map<LocalDate, List<DietCollection>>>> {
        val query = db.collection("diet_collection")
            .orderBy("created_at", Query.Direction.DESCENDING)

        return query.snapshots()
            .map { snapshot ->
                val mealList = snapshot.documents.mapNotNull { document ->
                    val dto = document.toObject<DietCollectionDTO>()
                    dto?.toDomainDietCollection()?.copy(
                        documentId = document.id
                    )
                }

                val mealsGroupedByDate = mealList.groupBy { dietCollection ->
                    Instant.ofEpochMilli(dietCollection.createdAt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }
                DataResourceResult.Success(mealsGroupedByDate) as DataResourceResult<Map<LocalDate, List<DietCollection>>>
            }
            .catch { exception ->
                emit(DataResourceResult.Error(exception))
            }
    }

    override suspend fun addMeal(mealData: DietCollection): DataResourceResult<Unit> {
        return try {
            val dto = mealData.toFirestoreDietCollectionDTO()
            db.collection("diet_collection").add(dto).await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Error(e)
        }
    }

    override suspend fun removeMeal(documentId: String): DataResourceResult<Unit> {
        return try {
            db.collection("diet_collection")
                .document(documentId)
                .delete()
                .await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Error(e)
        }
    }
}