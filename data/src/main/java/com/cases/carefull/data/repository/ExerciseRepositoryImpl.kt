package com.cases.carefull.data.repository

import com.cases.carefull.data.mapper.toDomainPose
import com.cases.carefull.data.model.ExerciseCollectionDTO
import com.cases.carefull.data.model.toDomainExerciseCollectionList
import com.cases.carefull.data.model.toFirestoreExerciseCollectionDTO
import com.cases.carefull.domain.model.exercise.ExerciseCollection
import com.cases.carefull.domain.model.exercise.ExerciseState
import com.cases.carefull.domain.model.exercise.ExerciseType
import com.cases.carefull.domain.model.exercise.Pose
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.util.DataResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetector
import kotlinx.coroutines.tasks.await

class ExerciseRepositoryImpl(
	private val poseDetector: PoseDetector
) : ExerciseRepository {
	
	private val db = Firebase.firestore
	override fun angleAnalyze(pose: Pose): ExerciseState {
		TODO("Not yet implemented")
	}
	
	override suspend fun analyzeImage(image: Any): Result<Pose> {
		if (image !is InputImage) return Result.failure(IllegalArgumentException("Image must be InputImage"))
		
		return try {
			val mlKitPose =
				poseDetector.process(image).await()
			val domainPose = mlKitPose.toDomainPose()
			Result.success(domainPose)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
	
	override suspend fun getAllExercise(userId: String): DataResult<List<ExerciseCollection>> =
		runCatching {
			val snapshot = db.collection("work_out_collection")
				.whereEqualTo("user_id", userId)
				.get()
				.await()
			val dtoList = snapshot.toObjects(ExerciseCollectionDTO::class.java)
			
			dtoList.toDomainExerciseCollectionList()
		}.map { exerciseList ->
			DataResult.Success(exerciseList)
			
		}.getOrElse { exception ->
			DataResult.Error(exception)
		}
	
	override suspend fun getDailyExerciseList(sports: String): DataResult<List<ExerciseType>> {
		TODO("Not yet implemented")
	}
	
	override suspend fun addExerciseRecord(exerciseRecord: ExerciseCollection): DataResult<Unit> {
		return try {
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
			DataResult.Success(Unit)
		} catch (e: Exception) {
			DataResult.Error(e)
		}
	}
	
	override suspend fun resetExercise(exercise: ExerciseCollection): DataResult<Unit> {
		TODO("Not yet implemented")
	}
}
