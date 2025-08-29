package com.cases.carefull.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.cases.carefull.BuildConfig
import com.cases.carefull.data.datasource.KakaoDataSourceImpl
import com.cases.carefull.data.firestore.UserDataSourceImpl
import com.cases.carefull.data.network.DietRetrofitClient
import com.cases.carefull.data.network.RetrofitInstance
import com.cases.carefull.data.repository.DietRepositoryImpl
import com.cases.carefull.data.repository.ExerciseRepositoryImpl
import com.cases.carefull.data.repository.MedicineRepositoryImpl
import com.cases.carefull.data.repository.UserRepositoryImpl
import com.cases.carefull.domain.repository.DietRepository
import com.cases.carefull.domain.repository.ExerciseRepository
import com.cases.carefull.domain.repository.MedicineRepository
import com.cases.carefull.domain.repository.UserRepository
import com.cases.carefull.data.repository.RankingRepositoryImpl
import com.cases.carefull.domain.repository.RankingRepository
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
    val userRepository: UserRepository
    val rankingRepository: RankingRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

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

    override val poseDetector: PoseDetector by lazy {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    override val dietRepository: DietRepository by lazy {
        DietRepositoryImpl(
            apiService = DietRetrofitClient.api,
            dietApiKey = BuildConfig.diet_api_key,
            poseDetector = poseDetector
        )
    }

    override val exerciseRepository: ExerciseRepository by lazy {
        ExerciseRepositoryImpl(
            context = context
        )
    }

    override val userRepository: UserRepository by lazy {
        val kakaoDataSource = KakaoDataSourceImpl()
        val userDataSource = UserDataSourceImpl()

        UserRepositoryImpl(
            context = context.applicationContext,
            kakaoDataSource = kakaoDataSource,
            userDataSource = userDataSource
        )
    }

    override val rankingRepository: RankingRepository by lazy {
        RankingRepositoryImpl(
        )
    }

    override val medicineViewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactory(
            navigationRepository = navigationRepository,
            medicineSearchUseCase = medicineSearchUseCase,
            dietRepository = dietRepository,
            exerciseRepository = exerciseRepository,
            userRepository = userRepository,
            rankingRepository = rankingRepository
        )
    }
}