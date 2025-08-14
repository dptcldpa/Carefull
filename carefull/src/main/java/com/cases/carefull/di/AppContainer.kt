package com.cases.carefull.di

import androidx.lifecycle.ViewModelProvider
import com.cases.carefull.BuildConfig
import com.cases.carefull.data.network.DietRetrofitClient
import com.cases.carefull.data.network.RetrofitInstance
import com.cases.carefull.data.repository.DietRepositoryImpl
import com.cases.carefull.data.repository.ExerciseRepositoryImpl
import com.cases.carefull.data.repository.MedicineRepositoryImpl
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.domain.usecase.MedicineSearchUseCase
import com.cases.carefull.features.carefullcommon.components.NavigationRepositoryImpl
import com.cases.carefull.features.carefullcommon.model.NavigationRepository
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

interface AppContainer {
    val navigationRepository: NavigationRepository
    val medicineRepository: MedicineRepository
    val medicineSearchUseCase: MedicineSearchUseCase
    val medicineViewModelFactory: ViewModelProvider.Factory
    val dietRepository: DietRepository
    val exerciseRepository: ExerciseRepository
    val poseDetector: PoseDetector
}

class DefaultAppContainer() : AppContainer {

    override val navigationRepository: NavigationRepository by lazy {
        NavigationRepositoryImpl()
    }

    private val medicineApiService by lazy {
        RetrofitInstance.medicineApi
    }

    override val medicineRepository: MedicineRepository by lazy {
        MedicineRepositoryImpl(medicineApiService)
    }

    override val medicineSearchUseCase: MedicineSearchUseCase by lazy {
        MedicineSearchUseCase(
            repository = medicineRepository,
            medicineApiKey = BuildConfig.medicine_api_key
        )
    }
    override val dietRepository: DietRepository by lazy {
        DietRepositoryImpl(
            apiService = DietRetrofitClient.api,
            dietApiKey = BuildConfig.diet_api_key
        )
    }

    // 2. PoseDetector 인스턴스 생성
    override val poseDetector: PoseDetector by lazy {
       val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }
    
    override val exerciseRepository: ExerciseRepository by lazy {
        ExerciseRepositoryImpl(poseDetector)
    }
 
	override val medicineViewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactory(
			navigationRepository = navigationRepository,
			medicineSearchUseCase = medicineSearchUseCase,
			dietRepository = dietRepository,
			exerciseRepository = exerciseRepository
		)
    }
}