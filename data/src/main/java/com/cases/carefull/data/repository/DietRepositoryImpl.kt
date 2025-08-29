package com.cases.carefull.data.repository

import android.content.Context
import com.cases.carefull.data.dao.BmrDao
import com.cases.carefull.data.database.BmrDatabase
import com.cases.carefull.data.mapper.toDataModel
import com.cases.carefull.data.model.DietCollectionDTO
import com.cases.carefull.data.model.toDomainDietCollectionList
import com.cases.carefull.data.model.toFirestoreDietCollectionDTO
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toDomainModel
import com.cases.carefull.data.mapper.toDomainPose
import com.cases.carefull.data.model.DietItemDto
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class DietRepositoryImpl(
    private val apiService: DietApiService,
    private val dietApiKey: String,
    private val poseDetector: PoseDetector,
    private val context: Context
) : DietRepository {
    private val db = Firebase.firestore
    
    private val bmrDao: BmrDao = BmrDatabase.getInstance(context).bmrDao()
    
    override suspend fun getAllMeal(): DataResourceResult<List<DietCollection>> =
        runCatching {
            val snapshot = db.collection("diet_collection").get().await()
            val dtoList = snapshot.toObjects(DietCollectionDTO::class.java)

            dtoList.toDomainDietCollectionList()
        }.map { dietList ->
            DataResourceResult.Success(dietList)

        }.getOrElse { exception ->
            DataResourceResult.Error(exception)
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

    override suspend fun searchMeals(
        query: String
    ): List<DietCollection> = runCatching {
        val response = apiService.getFoodList(
            apiKey = dietApiKey,
            foodName = query
        )
        if (response.header.resultCode == "00" && response.body.items.isNotEmpty()) {
            val dtoList: List<DietItemDto> = response.body.items
            dtoList.map { it.toDomain() }
        } else {
            emptyList()
        }
    }.getOrElse {
        emptyList()
    }

    override suspend fun analyzeImage(image: Any): DataResourceResult<Pose> =
        runCatching {
            if (image !is InputImage) return DataResourceResult.Error(IllegalArgumentException("Image must be InputImage"))
            val mlKitPose =
                poseDetector.process(image).await()
            mlKitPose.toDomainPose()
        }.map { domainPose ->
            DataResourceResult.Success(domainPose)
        }.getOrElse { exception ->
            DataResourceResult.Error(exception)
        }

    override suspend fun removeMeal(mealData: DietCollection) {
        TODO("Not yet implemented")
    }
    
    override suspend fun getMyBmr(userId: String): Flow<Bmr?> {
        return bmrDao.getBmrByUserId(userId).map { bmrCollection ->
            bmrCollection?.toDomainModel()
        }
    }
    
    override suspend fun insertBmr(bmr: Bmr) {
        val bmrCollection = bmr.toDataModel()
        bmrDao.insertBmr(bmrCollection)
    }
}