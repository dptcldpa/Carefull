package com.cases.carefull.data.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.cases.carefull.data.dao.BmrDao
import com.cases.carefull.data.dao.FavoriteMealDao
import com.cases.carefull.data.dao.RecentMealSearchDao
import com.cases.carefull.data.database.AppDatabase
import com.cases.carefull.data.di.DietApiKey
import com.cases.carefull.data.mapper.toDataModel
import com.cases.carefull.data.mapper.toDomain
import com.cases.carefull.data.mapper.toDomainModel
import com.cases.carefull.data.mapper.toDomainPose
import com.cases.carefull.data.mapper.toEntity
import com.cases.carefull.data.dto.DietCollectionDTO
import com.cases.carefull.data.dto.DietItemDto
import com.cases.carefull.data.dto.toDomainDietCollection
import com.cases.carefull.data.dto.toFirestoreDietCollectionDTO
import com.cases.carefull.data.network.DietApiService
import com.cases.carefull.domain.model.diet.Bmr
import com.cases.carefull.domain.model.diet.DietCollection
import com.cases.carefull.domain.model.diet.FavoriteMeal
import com.cases.carefull.domain.model.diet.RecentMealSearch
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.util.DataResourceResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetector
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class DietRepositoryImpl @Inject constructor(
	private val apiService: DietApiService,
	@DietApiKey private val dietApiKey: String,
	private val poseDetector: PoseDetector,
	@ApplicationContext private val context: Context
) : DietRepository {
	private val db = Firebase.firestore
	
	private val bmrDao: BmrDao = AppDatabase.getInstance(context).bmrDao()
	private val favoriteMealDao: FavoriteMealDao =
		AppDatabase.getInstance(context).favoriteMealDao()
	
	private val recentMealSearchDao: RecentMealSearchDao =
		AppDatabase.getInstance(context).recentMealSearchDao()
	
	@RequiresApi(Build.VERSION_CODES.O)
	override suspend fun getAllMeal(): Flow<DataResourceResult<Map<LocalDate, List<DietCollection>>>> {
		val query = db.collection("diet_collection")
			.orderBy("created_at", Query.Direction.DESCENDING)
		
		return query.snapshots().map { snapshot ->
			try {
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
				DataResourceResult.Success(mealsGroupedByDate)
			} catch (e: Exception) {
				DataResourceResult.Error(e)
			}
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
	
	override suspend fun getMyBmr(userId: String): Flow<Bmr?> {
		return bmrDao.getBmrByUserId(userId).map { bmrCollection ->
			bmrCollection?.toDomainModel()
		}
	}
	
	override suspend fun insertBmr(bmr: Bmr) {
		val bmrCollection = bmr.toDataModel()
		bmrDao.insertBmr(bmrCollection)
	}
	
	override fun getFavorites(): Flow<List<FavoriteMeal>> {
		return favoriteMealDao.getAll().map { entities ->
			entities.map { it.toDomain() }
		}
	}
	
	override suspend fun addFavorite(meal: FavoriteMeal) {
		favoriteMealDao.insert(meal.toEntity())
	}
	
	override suspend fun deleteFavorite(meal: FavoriteMeal) {
		favoriteMealDao.delete(meal.toEntity())
	}
	
	override fun getRecentSearches(): Flow<List<RecentMealSearch>> {
		return recentMealSearchDao.getAll().map { entities -> entities.map { it.toDomain() } }
	}
	
	override suspend fun addSearch(query: String) {
		recentMealSearchDao.insertOrUpdate(query.trim())
	}
	
	override suspend fun deleteSearch(search: RecentMealSearch) {
		recentMealSearchDao.delete(search.toEntity())
	}
	
	override suspend fun clearAllSearches() {
		recentMealSearchDao.clearAll()
	}
}
